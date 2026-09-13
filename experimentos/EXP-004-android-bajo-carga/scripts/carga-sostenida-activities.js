import http from 'k6/http';
import { check, fail } from 'k6';
import { Trend, Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const VUS = Number(__ENV.VUS || 499);
const DURATION = __ENV.DURATION || '60s';
const RESULT_JSON =
    __ENV.RESULT_JSON ||
    'experimentos/EXP-004-android-bajo-carga/resultados/k6-sostenido.json';

if (!Number.isInteger(VUS) || VUS < 1 || VUS > 499) {
    throw new Error('VUS debe estar entre 1 y 499.');
}

const activityGetDuration =
    new Trend('activity_get_duration', true);

const getSuccess =
    new Counter('activity_get_success');

const activityCountOk =
    new Counter('activity_count_ok');

export const options = {
    setupTimeout: '5m',
    scenarios: {
        sustainedActivities: {
            executor: 'constant-vus',
            vus: VUS,
            duration: DURATION,
            gracefulStop: '5s',
        },
    },

    summaryTrendStats: [
        'avg',
        'min',
        'med',
        'p(90)',
        'p(95)',
        'max',
    ],
};

function emailForVu(vuNumber) {
    const userNumber = vuNumber + 1;

    return (
        'loadtest' +
        String(userNumber).padStart(3, '0') +
        '@rachapro.test'
    );
}

export function setup() {
    const password = __ENV.K6_PASSWORD;

    if (!password) {
        fail('Falta K6_PASSWORD.');
    }

    const tokens = [];
    const batchSize = 50;

    for (let start = 1; start <= VUS; start += batchSize) {
        const end = Math.min(
            start + batchSize - 1,
            VUS
        );

        const requests = [];

        for (let vu = start; vu <= end; vu++) {
            requests.push({
                method: 'POST',
                url: `${BASE_URL}/api/auth/login`,
                body: JSON.stringify({
                    email: emailForVu(vu),
                    password: password,
                }),
                params: {
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    tags: {
                        name: 'POST /api/auth/login',
                    },
                },
            });
        }

        const responses = http.batch(requests);

        for (let i = 0; i < responses.length; i++) {
            const response = responses[i];

            if (response.status !== 200) {
                fail(
                    `Login fallido para VU ${start + i}. ` +
                    `HTTP ${response.status}`
                );
            }

            const token = response.json('token');

            if (!token) {
                fail(
                    `Login sin token para VU ${start + i}.`
                );
            }

            tokens.push(token);
        }
    }

    if (tokens.length !== VUS) {
        fail(
            `Se esperaban ${VUS} tokens y se obtuvieron ` +
            `${tokens.length}.`
        );
    }

    return {
        tokens: tokens,
    };
}

export default function (data) {
    const token = data.tokens[__VU - 1];

    if (!token) {
        fail(`No existe token para VU ${__VU}.`);
    }

    if (__VU === 1 && __ITER === 0) {
        console.log(
            `EXP004_LOAD_PHASE_START epochMs=${Date.now()}`
        );
    }

    const response = http.get(
        `${BASE_URL}/api/activities`,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
            tags: {
                name: 'GET /api/activities',
            },
        }
    );

    activityGetDuration.add(
        response.timings.duration
    );

    const statusOk = response.status === 200;

    if (statusOk) {
        getSuccess.add(1);
    }

    let countOk = false;

    if (statusOk) {
        try {
            const body = response.json();

            countOk =
                Array.isArray(body) &&
                body.length === 1000;
        }
        catch (_) {
            countOk = false;
        }
    }

    if (countOk) {
        activityCountOk.add(1);
    }

    check(response, {
        'GET activities responde 200':
            () => statusOk,

        'GET activities devuelve 1000':
            () => countOk,
    });
}

export function teardown() {
    console.log(
        `EXP004_LOAD_PHASE_END epochMs=${Date.now()}`
    );
}

export function handleSummary(data) {
    const metricValues = (name) => {
        return data.metrics[name]
            ? data.metrics[name].values
            : null;
    };

    const result = {
        experiment: 'EXP-004',
        workload: {
            vus: VUS,
            duration: DURATION,
            androidUser:
                'loadtest001@rachapro.test',
            k6Users:
                `loadtest002..loadtest${String(
                    VUS + 1
                ).padStart(3, '0')}@rachapro.test`,
        },
        metrics: {
            activity_get_duration:
                metricValues(
                    'activity_get_duration'
                ),

            activity_get_success:
                metricValues(
                    'activity_get_success'
                ),

            activity_count_ok:
                metricValues(
                    'activity_count_ok'
                ),

            checks:
                metricValues('checks'),

            http_req_failed:
                metricValues('http_req_failed'),

            http_reqs:
                metricValues('http_reqs'),
        },
    };

    const json =
        JSON.stringify(result, null, 2);

    return {
        stdout: json + '\n',
        [RESULT_JSON]: json,
    };
}