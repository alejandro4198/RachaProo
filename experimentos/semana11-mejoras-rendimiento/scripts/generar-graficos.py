#!/usr/bin/env python3
import csv
import os
from pathlib import Path

ROOT = Path(r"C:\Users\Lenovo\AndroidStudioProjects\RachaPro")
CSV_PATH = ROOT / "experimentos" / "semana11-mejoras-rendimiento" / "02-throughput-jitter" / "throughput-historico.csv"
OUT_DIR = ROOT / "experimentos" / "semana11-mejoras-rendimiento" / "graficos"
OUT_DIR.mkdir(parents=True, exist_ok=True)

# Try to use matplotlib if available
try:
    import matplotlib
    matplotlib.use('Agg')
    import matplotlib.pyplot as plt
    HAS_MPL = True
except Exception:
    HAS_MPL = False

# Read P95 from A1
p95_map = {}
try:
    import re
    a1 = ROOT / "experimentos" / "semana11-crecimiento-post-activities" / "A1-resultados-crecimiento.md"
    with open(a1, encoding='utf-8', errors='ignore') as f:
        content = f.read()
    # rough parse
    for m in re.finditer(r'\|\s*(\d+)\s*\|[^|]*\|[^|]*\|[^|]*\|\s*([0-9]+\.?[0-9]*)\s*\|', content):
        vu = int(m.group(1))
        p95 = float(m.group(2))
        p95_map[vu] = p95
except Exception:
    pass

# Read throughput
thr_map = {}
try:
    with open(CSV_PATH, encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            try:
                vu = int(row['VU']) if row['VU'] else None
                if not vu:
                    continue
                if row['status'] == 'WARMUP' or row['run'] == 'R1':
                    continue  # don't use for representative
                thr = float(row['throughput_req_s']) if row['throughput_req_s'] else None
                if thr is not None:
                    thr_map[vu] = thr
            except Exception:
                continue
except Exception:
    pass

if HAS_MPL:
    # VUs vs P95
    try:
        xs = sorted(p95_map.keys())
        ys = [p95_map[x] for x in xs]
        plt.figure(figsize=(8,5))
        plt.plot(xs, ys, marker='o')
        plt.xlabel('VUs')
        plt.ylabel('P95 (ms)')
        plt.title('VUs vs P95 (fuente: A1-resultados-crecimiento.md)')
        plt.grid(True, alpha=0.3)
        plt.savefig(OUT_DIR / 'vus-vs-p95.png', dpi=150, bbox_inches='tight')
        plt.close()
    except Exception as e:
        print('ERR1', e)
    
    # VUs vs throughput (only if sufficient)
    try:
        if len(thr_map) >= 2:
            xs = sorted(thr_map.keys())
            ys = [thr_map[x] for x in xs]
            plt.figure(figsize=(8,5))
            plt.plot(xs, ys, marker='o')
            plt.xlabel('VUs')
            plt.ylabel('Throughput (req/s)')
            plt.title('VUs vs Throughput (desde evidencia k6 verificada)')
            plt.grid(True, alpha=0.3)
            plt.savefig(OUT_DIR / 'vus-vs-throughput.png', dpi=150, bbox_inches='tight')
            plt.close()
        else:
            # write note
            with open(OUT_DIR / 'vus-vs-throughput.txt', 'w') as f:
                f.write('THROUGHPUT REPRESENTATIVO: PENDIENTE POR FALTA DE EVIDENCIA (solo R1/warmup disponible para verificación)\n')
    except Exception as e:
        print('ERR2', e)
    
    # jitter - placeholder note
    with open(OUT_DIR / 'vus-vs-jitter.txt', 'w') as f:
        f.write('JITTER: ANÁLISIS SECUNDARIO POST HOC - calculado solo si existen R2,R3,R4 para todos los niveles; no verificado completamente desde muestras.json\n')
    
    with open(OUT_DIR / 'vus-vs-error-rate.txt', 'w') as f:
        f.write('ERROR RATE: 100% éxito / 0% 5xx según A1-resultados-crecimiento.md; sin verificación desde todas las muestras.json\n')
else:
    with open(OUT_DIR / 'README.txt', 'w') as f:
        f.write('matplotlib no disponible; gráficos no generados\n')

print('done')
