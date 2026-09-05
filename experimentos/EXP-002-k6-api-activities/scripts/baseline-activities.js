import http from "k6/http";
import { check, fail } from "k6";
import { Trend, Rate } from "k6/metrics";

const createDuration = new Trend(
    "activity_create_duration",
    true
);

const createSuccess = new Rate(
    "activity_create_success"
);

export const options = {
    vus: 1,
    iterations: 25
};

export function setup() {
    const baseUrl = __ENV.BASE_URL;
    const email = __ENV.TEST_EMAIL;
    const password = __ENV.TEST_PASSWORD;

    if (!baseUrl || !email || !password) {
        fail(
            "Faltan BASE_URL, TEST_EMAIL o TEST_PASSWORD."
        );
    }

    const loginResponse = http.post(
        `${baseUrl}/api/auth/login`,
        JSON.stringify({
            email: email,
            password: password
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

    if (
        !check(loginResponse, {
            "login devuelve 200": (r) =>
                r.status === 200
        })
    ) {
        fail(
            `Login fallido. HTTP ${loginResponse.status}`
        );
    }

    const token =
        loginResponse.json("token");

    if (!token) {
        fail(
            "El login no devolvió un token."
        );
    }

    const categoriesResponse = http.get(
        `${baseUrl}/api/categories`,
        {
            headers: {
                Authorization:
                    `Bearer ${token}`
            },
            tags: {
                name: "SETUP categories"
            }
        }
    );

    if (
        !check(categoriesResponse, {
            "categories devuelve 200": (r) =>
                r.status === 200
        })
    ) {
        fail(
            `Consulta de categorías fallida. HTTP ${categoriesResponse.status}`
        );
    }

    const categories =
        categoriesResponse.json();

    if (
        !Array.isArray(categories) ||
        categories.length === 0
    ) {
        fail(
            "El usuario de prueba no tiene categorías activas."
        );
    }

    return {
        baseUrl: baseUrl,
        token: token,
        categoryId: categories[0].id,
        runId: `${Date.now()}`
    };
}

export default function (data) {
    const epochDay =
        Math.floor(
            Date.now() / 86400000
        );

    const requestBody =
        JSON.stringify({
            categoryId:
                data.categoryId,

            title:
                `EXP-002-${data.runId}-${__ITER + 1}`,

            description:
                "Actividad creada por EXP-002 k6",

            dueDateEpochDay:
                epochDay + 1,

            dueTimeMinutes:
                null,

            priority:
                "MEDIUM",

            repeatRule:
                null
        });

    const response = http.post(
        `${data.baseUrl}/api/activities`,
        requestBody,
        {
            headers: {
                "Content-Type":
                    "application/json",

                Authorization:
                    `Bearer ${data.token}`
            },
            tags: {
                name:
                    "POST /api/activities"
            }
        }
    );

    createDuration.add(
        response.timings.duration
    );

    const successful =
        response.status === 201;

    createSuccess.add(
        successful
    );

    check(response, {
        "actividad devuelve 201": (r) =>
            r.status === 201,

        "actividad devuelve id": (r) => {
            if (r.status !== 201) {
                return false;
            }

            return r.json("id") != null;
        }
    });
}

export function teardown(data) {
    const response = http.get(
        `${data.baseUrl}/api/activities`,
        {
            headers: {
                Authorization:
                    `Bearer ${data.token}`
            },
            tags: {
                name:
                    "TEARDOWN activities"
            }
        }
    );

    if (response.status !== 200) {
        return;
    }

    const activities =
        response.json();

    activities
        .filter(
            (activity) =>
                activity.title.startsWith(
                    `EXP-002-${data.runId}-`
                )
        )
        .forEach(
            (activity) => {

                http.del(
                    `${data.baseUrl}/api/activities/${activity.id}`,
                    null,
                    {
                        headers: {
                            Authorization:
                                `Bearer ${data.token}`
                        },
                        tags: {
                            name:
                                "TEARDOWN delete"
                        }
                    }
                );
            }
        );
}