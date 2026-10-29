#!/usr/bin/env python3
import json
import csv
import os
from datetime import datetime
from pathlib import Path

try:
    from dateutil import parser
except Exception:
    parser = None

ROOT = Path(r"C:\Users\Lenovo\AndroidStudioProjects\RachaPro")
EXP = ROOT / "experimentos"

OUT_DIR = ROOT / "experimentos" / "semana11-mejoras-rendimiento" / "02-throughput-jitter"
OUT_CSV = OUT_DIR / "throughput-historico.csv"

# Look for muestras.json with http_reqs
candidates = []
for p in EXP.rglob("muestras.json"):
    try:
        candidates.append(p)
    except Exception:
        pass

rows = []

def parse_time(t):
    if parser is not None:
        try:
            return parser.isoparse(t)
        except Exception:
            pass
    # fallback
    try:
        # handle common formats
        if t.endswith('Z'):
            t = t[:-1] + '+00:00'
        return datetime.fromisoformat(t.replace('Z', '+00:00'))
    except Exception:
        return None

for c in candidates:
    try:
        first = None
        last = None
        c_count = 0
        # try to detect VU and run from path
        path_str = str(c)
        vu = ""
        run = ""
        # look for patterns like S11-VU1000-R1 or VU1000-R1
        import re
        m = re.search(r'VU(\d+)[-_]R([1234])', path_str, re.IGNORECASE)
        if m:
            vu = m.group(1)
            run = "R" + m.group(2)
        # also try other patterns
        with open(c, encoding='utf-8') as f:
            for l in f:
                if '"metric":"http_reqs"' in l and '"type":"Point"' in l:
                    try:
                        o = json.loads(l)
                        tags = o.get('data', {}).get('tags', {}) or {}
                        name = tags.get('name', '') or o.get('metric', '')
                        # prefer POST /api/activities if tagged
                        if tags.get('name') == 'POST /api/activities' or name == 'POST /api/activities':
                            c_count += 1
                            t = o['data']['time']
                            if first is None:
                                first = t
                            last = t
                        elif c_count == 0 and name == 'http_reqs' and not first:
                            # fallback: count all http_reqs points if no tag filter gives results? but prefer tagged
                            pass
                    except Exception:
                        pass
        # if no tagged found, try counting all http_reqs points? but need to be careful - use as fallback only if clearly relevant
        if c_count == 0:
            first = None
            last = None
            with open(c, encoding='utf-8') as f:
                for l in f:
                    if '"metric":"http_reqs"' in l and '"type":"Point"' in l:
                        try:
                            o = json.loads(l)
                            tags = o.get('data', {}).get('tags', {}) or {}
                            # skip setup/health if obvious? include all by default
                            c_count += 1
                            t = o['data']['time']
                            if first is None:
                                first = t
                            last = t
                        except Exception:
                            pass
        if first and last and c_count > 0:
            fdt = parse_time(first)
            ldt = parse_time(last)
            if fdt and ldt:
                dur = (ldt - fdt).total_seconds()
                if dur > 0:
                    thr = c_count / dur
                    status = "OK"
                    if run and run.upper() == "R1":
                        status = "WARMUP"  # exclude from representative
                    rows.append({
                        "VU": vu,
                        "run": run,
                        "source": str(c.relative_to(ROOT)),
                        "http_reqs": c_count,
                        "start_time": first,
                        "end_time": last,
                        "duration_seconds": f"{dur:.6f}",
                        "throughput_req_s": f"{thr:.6f}",
                        "status": status
                    })
                else:
                    rows.append({
                        "VU": vu,
                        "run": run,
                        "source": str(c.relative_to(ROOT)),
                        "http_reqs": c_count,
                        "start_time": first,
                        "end_time": last,
                        "duration_seconds": "",
                        "throughput_req_s": "",
                        "status": "PENDIENTE"
                    })
            else:
                rows.append({
                    "VU": vu,
                    "run": run,
                    "source": str(c.relative_to(ROOT)),
                    "http_reqs": c_count,
                    "start_time": first,
                    "end_time": last,
                    "duration_seconds": "",
                    "throughput_req_s": "",
                    "status": "PENDIENTE"
                })
        else:
            rows.append({
                "VU": vu,
                "run": run,
                "source": str(c.relative_to(ROOT)),
                "http_reqs": c_count,
                "start_time": first or "",
                "end_time": last or "",
                "duration_seconds": "",
                "throughput_req_s": "",
                "status": "PENDIENTE"
            })
    except Exception as e:
        rows.append({
            "VU": "",
            "run": "",
            "source": str(c.relative_to(ROOT)),
            "http_reqs": "",
            "start_time": "",
            "end_time": "",
            "duration_seconds": "",
            "throughput_req_s": "",
            "status": f"PENDIENTE:{e}"
        })

OUT_DIR.mkdir(parents=True, exist_ok=True)
fieldnames = ["VU", "run", "source", "http_reqs", "start_time", "end_time", "duration_seconds", "throughput_req_s", "status"]
with open(OUT_CSV, 'w', newline='', encoding='utf-8') as f:
    w = csv.DictWriter(f, fieldnames=fieldnames)
    w.writeheader()
    for r in rows:
        w.writerow(r)

print(f"WROTE {OUT_CSV} rows={len(rows)}")
for r in rows:
    print(r)
