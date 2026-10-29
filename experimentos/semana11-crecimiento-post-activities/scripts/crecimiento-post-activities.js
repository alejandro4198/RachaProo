import http from "k6/http";
import { fail, sleep } from "k6";
import { Trend, Rate, Counter } from "k6/metrics";

const createDuration = new Trend("activity_create_duration", true);
const createSuccess = new Rate("activity_create_success");

const http2xx = new Counter("activity_http_2xx");
const http4xx = new Counter("activity_http_4xx");
const http5xx = new Counter("activity_http_5xx");

const vus = Number(__ENV.S11_VUS || 1);

export const options = {
    setupTimeout: "30m",
    scenarios: {
        crecimiento: {
            executor: "constant-vus",
            vus: vus,
            duration: "60s",
            gracefulStop: "30s"
        }
    }
};

function autenticar(baseUrl, email, password) {
    const response = http.post(
        `${baseUrl}/api/auth/login`,
        JSON.stringify({
            email,
            password
        }),
        {
            headers: {
                "Content-Type": "application/json"
            },
            tags: {
                name: "SETUP login"
            }
        }
    );

    if (response.status !== 200) {
        fail(`Login fallido. HTTP ${response.status}`);
    }

    let token = null;

    try {
        token = response.json("token");
    } catch (_) {
        fail("El login no devolvio JSON valido.");
    }

    if (!token) {
        fail("El login no devolvio token.");
    }

    return token;
}

function obtenerCategoria(baseUrl, token) {
    const response = http.get(
        `${baseUrl}/api/categories`,
        {
            headers: {
                Authorization: `Bearer ${token}`
            },
            tags: {
                name: "SETUP categories"
            }
        }
    );

    if (response.status !== 200) {
        fail(`Categorias: HTTP ${response.status}`);
    }

    let categories = null;

    try {
        categories = response.json();
    } catch (_) {
        fail("Categorias no devolvio JSON valido.");
    }

    if (!Array.isArray(categories) || categories.length !== 1) {
        fail("Se esperaba exactamente una categoria activa.");
    }

    const categoryId = categories[0].id;

    if (!Number.isInteger(categoryId) || categoryId <= 0) {
        fail("La categoria no tiene un ID valido.");
    }

    return categoryId;
}

function correoSintetico(grupo, numero) {
    return `loadtest-${grupo}-${String(numero).padStart(4, "0")}@rachapro.test`;
}

function generarCorreos(grupo, cantidad) {
    const correos = [];

    for (let numero = 1; numero <= cantidad; numero++) {
        correos.push(
            correoSintetico(
                grupo,
                numero
            )
        );
    }

    return correos;
}

function construirIdentidades(vus) {
    const distribuciones = {
        1: {
            high: 1,
            low: 0
        },
        2: {
            high: 1,
            low: 1
        },
        10: {
            high: 2,
            low: 8
        },
        50: {
            high: 10,
            low: 40
        },
        100: {
            high: 20,
            low: 80
        },
        250: {
            high: 50,
            low: 200
        },
        500: {
            high: 100,
            low: 400
        },
        750: {
            high: 150,
            low: 600
        },
        1000: {
            high: 200,
            low: 800
        }
    };

    const distribucion = distribuciones[vus];

    if (!distribucion) {
        fail(`Nivel S11_VUS no preregistrado: ${vus}`);
    }

    const high = generarCorreos(
        "high",
        distribucion.high
    );

    const low = generarCorreos(
        "low",
        distribucion.low
    );

    const correos = [
        ...high,
        ...low
    ];

    if (correos.length !== vus) {
        fail(
            `Cantidad de identidades incorrecta: ${correos.length}, esperadas ${vus}`
        );
    }

    if (new Set(correos).size !== correos.length) {
        fail("Se detectaron identidades duplicadas.");
    }

    return correos;
}

function obtenerActivityId(response) {
    if (response.status !== 201) {
        return null;
    }

    try {
        const id = response.json("id");

        if (Number.isInteger(id) && id > 0) {
            return id;
        }
    } catch (_) {
        return null;
    }

    return null;
}

export function setup() {
    const baseUrl = __ENV.BASE_URL;
    const password = __ENV.TEST_PASSWORD;
    const runId = __ENV.S11_RUN_ID;

    if (!baseUrl || !password || !runId) {
        fail(
            "Faltan BASE_URL, TEST_PASSWORD o S11_RUN_ID."
        );
    }

    const correos = construirIdentidades(vus);

    // Batch 1: Batch login for all identities concurrently
    const loginReqs = correos.map((email) => ({
        method: "POST",
        url: `${baseUrl}/api/auth/login`,
        body: JSON.stringify({ email, password }),
        params: {
            headers: { "Content-Type": "application/json" },
            tags: { name: "SETUP login" }
        }
    }));

    const loginResponses = http.batch(loginReqs);

    // Batch 2: Batch category retrieval for all logged-in tokens
    const categoryReqs = loginResponses.map((res, i) => {
        if (res.status !== 200) {
            fail(`Login setup fallido para ${correos[i]}: HTTP ${res.status}`);
        }
        let token = null;
        try {
            token = res.json("token");
        } catch (_) {
            fail("Login no devolvió JSON válido");
        }
        if (!token) fail("Token nulo en setup");

        return {
            method: "GET",
            url: `${baseUrl}/api/categories`,
            params: {
                headers: { Authorization: `Bearer ${token}` },
                tags: { name: "SETUP categories" }
            },
            token
        };
    });

    const categoryResponses = http.batch(categoryReqs);

    const identidades = categoryResponses.map((res, i) => {
        if (res.status !== 200) {
            fail(`Categorias setup fallido: HTTP ${res.status}`);
        }
        let categories = null;
        try {
            categories = res.json();
        } catch (_) {
            fail("Categorias no devolvió JSON válido");
        }
        if (!Array.isArray(categories) || categories.length < 1) {
            fail("Se esperaba al menos una categoria activa");
        }
        const categoryId = categories[0].id;
        return {
            token: categoryReqs[i].token,
            categoryId
        };
    });

    if (identidades.length !== vus) {
        fail(
            `Setup preparo ${identidades.length} identidades, esperadas ${vus}`
        );
    }

    return {
        baseUrl,
        runId,
        identidades
    };
}

export default function (data) {
    const identidad = data.identidades[__VU - 1];

    if (!identidad) {
        fail(`No hay identidad asignada al VU ${__VU}`);
    }

    const payload = {
        categoryId: identidad.categoryId,
        title: "S11 crecimiento POST activities",
        description: "Dato sintetico Semana 11",
        dueDateEpochDay: 20740,
        dueTimeMinutes: null,
        priority: "MEDIUM",
        repeatRule: null
    };

    const metricTags = {
        run_id: data.runId,
        s11_vus: String(vus)
    };

    const response = http.post(
        `${data.baseUrl}/api/activities`,
        JSON.stringify(payload),
        {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${identidad.token}`
            },
            tags: {
                name: "POST /api/activities",
                run_id: data.runId,
                s11_vus: String(vus)
            }
        }
    );

    createDuration.add(
        response.timings.duration,
        metricTags
    );

    const activityId = obtenerActivityId(response);

    const exitoso =
        response.status === 201 &&
        activityId !== null;

    createSuccess.add(
        exitoso,
        metricTags
    );

    if (
        response.status >= 200 &&
        response.status < 300
    ) {
        http2xx.add(
            1,
            metricTags
        );
    } else if (
        response.status >= 400 &&
        response.status < 500
    ) {
        http4xx.add(
            1,
            metricTags
        );
    } else if (
        response.status >= 500 &&
        response.status < 600
    ) {
        http5xx.add(
            1,
            metricTags
        );
    }

    sleep(1);
}