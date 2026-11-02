# Guía de Respaldo y Recuperación (Backup & Restore) — PostgreSQL

## 1. Procedimiento de Respaldo (`pg_dump`)
```bash
pg_dump -h <HOST> -p <PORT> -U <USER> -F c -b -v -f rachapro_backup.dump <DB_NAME>
```

## 2. Procedimiento de Restauración (`pg_restore`)
```bash
pg_restore -h <HOST> -p <PORT> -U <USER> -d <DB_NAME> -v --clean --if-exists rachapro_backup.dump
```

## 3. Criterio de Validación Posterior a Recuperación
- Verificación de tablas principales (`users`, `activities`, `pomodoro_sessions`, `reminders`, `achievements`).
- Conteo de registros por entidad equivalente al estado previo.
- Arranque exitoso del backend (`spring.jpa.hibernate.ddl-auto=validate` sin excepciones).
