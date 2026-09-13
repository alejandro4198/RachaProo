import http from 'k6/http';
import { check, fail } from 'k6';
import { Trend, Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const VUS = Number(__ENV.VUS || 499);
const DURATION = __ENV.DURATION || '60s';
const RESULT_JSON =
    __ENV.RESULT_JSON ||
    'experimentos/semana8-diagnostico-activities/resultados/timing-http.json';

if (!Number.isInteger(VUS) || VUS < 1 || VUS > 499) {
    throw new Error('VUS debe estar entre 1 y 499.');
}

const activityGetDuration =
    new Trend('activity_get_duration', true);

const activityGetBlocked =
    new Trend('activity_get_blocked', true);

const activityGetConnecting =
    new Trend('activity_get_connecting', true);

const activityGetSending =
    new Trend('activity_get_sending', true);

const activityGetWaiting =
    new Trend('activity_get_waiting', true);

const activityGetReceiving =
    new Trend('activity_get_receiving', true);

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
            `DIAG_ACTIVITY_HTTP_START epochMs=${Date.now()}`
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

    activityGetBlocked.add(response.timings.blocked);
    activityGetConnecting.add(response.timings.connecting);
    activityGetSending.add(response.timings.sending);
    activityGetWaiting.add(response.timings.waiting);
    activityGetReceiving.add(response.timings.receiving);

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
        `DIAG_ACTIVITY_HTTP_END epochMs=${Date.now()}`
    );
}

export function handleSummary(data) {
    const metricValues = (name) => {
        return data.metrics[name]
            ? data.metrics[name].values
            : null;
    };

    const result = {
        experiment: 'SEMANA8-DIAGNOSTICO-ACTIVITIES-HTTP',
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

            activity_get_blocked:
                metricValues('activity_get_blocked'),

            activity_get_connecting:
                metricValues('activity_get_connecting'),

            activity_get_sending:
                metricValues('activity_get_sending'),

            activity_get_waiting:
                metricValues('activity_get_waiting'),

            activity_get_receiving:
                metricValues('activity_get_receiving'),

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