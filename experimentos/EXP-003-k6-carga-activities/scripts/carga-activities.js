import http from 'k6/http';
import { check } from 'k6';
import exec from 'k6/execution';
import { Trend, Rate } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const PASSWORD = __ENV.LOADTEST_PASSWORD;
const VUS = Number(__ENV.VUS || 10);

const getDuration = new Trend('activity_get_duration', true);
const getSuccess = new Rate('activity_get_success');
const countCorrect = new Rate('activity_count_correct');

export const options = {
    setupTimeout: '15m',

    scenarios: {
        activities_get: {
            executor: 'per-vu-iterations',
            vus: VUS,
            iterations: 1,
            maxDuration: '2m',
        },
    },

    thresholds: {
        activity_get_success: ['rate==1'],
        activity_count_correct: ['rate==1'],
    },
};

function emailFor(index) {
    return `loadtest${String(index).padStart(3, '0')}@rachapro.test`;
}

export function setup() {
    if (!PASSWORD) {
        throw new Error('LOADTEST_PASSWORD no esta configurada');
    }

    const tokens = [];

    for (let start = 1; start <= VUS; start += 50) {
        const end = Math.min(start + 49, VUS);
        const requests = [];

        for (let i = start; i <= end; i++) {
            requests.push({
                method: 'POST',
                url: `${BASE_URL}/api/auth/login`,
                body: JSON.stringify({
                    email: emailFor(i),
                    password: PASSWORD,
                }),
                params: {
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    tags: {
                        phase: 'setup',
                        name: 'login',
                    },
                },
            });
        }

        const responses = http.batch(requests);

        for (const response of responses) {
            if (response.status !== 200) {
                throw new Error(`Login fallo con HTTP ${response.status}`);
            }

            const body = response.json();

            if (!body.token) {
                throw new Error('Login sin JWT');
            }

            tokens.push(body.token);
        }
    }

    return { tokens };
}

export default function (data) {
    const index = exec.vu.idInTest - 1;
    const token = data.tokens[index];

    const response = http.get(
        `${BASE_URL}/api/activities`,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
            tags: {
                phase: 'load',
                name: 'GET activities',
            },
        }
    );

    const success = response.status === 200;

    let correctCount = false;

    if (success) {
        try {
            const activities = response.json();

            correctCount =
                Array.isArray(activities) &&
                activities.length === 1000;
        } catch (_) {
            correctCount = false;
        }
    }

    getDuration.add(response.timings.duration);
    getSuccess.add(success);
    countCorrect.add(correctCount);

    check(response, {
        'GET activities HTTP 200': () => success,
        'usuario recibe 1000 actividades': () => correctCount,
    });
}