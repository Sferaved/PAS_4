# MantisBT в Docker — план развёртывания для PAS

Документ для продолжения работы на другом ПК.  
Связанный код PAS: `utils/bugreport/mantis/`, Firebase `keys/mantis_key`.

---

## Текущее состояние PAS (приложение)

| Режим | Когда |
|-------|--------|
| **Основной** | Mantis доступен + `keys/mantis_key` в Firebase → тикет в Mantis + Telegram со ссылкой |
| **Fallback** | Mantis/Firebase недоступны → **старый путь**: Telegram + файл `app_log.txt` |

Можно выкатывать PAS **до** развёртывания Mantis — логи не потеряются.

---

## Чеклист «где остановились»

Отмечайте по мере выполнения:

- [ ] **1.** Docker + docker compose на сервере (91.219.60.148 или отдельный VPS)
- [ ] **2.** Скопировать `deploy/mantis/docker-compose.yml`, сменить пароли
- [ ] **3.** `docker compose up -d` в `/opt/mantis`
- [ ] **4.** Nginx + HTTPS для `bugs.<домен>` → `127.0.0.1:8989`
- [ ] **5.** Веб-установщик Mantis (`/admin/install.php`)
- [ ] **6.** REST API: пользователь + API-токен
- [ ] **7.** Проект + категория для PAS
- [ ] **8.** Firebase `keys/mantis_key`
- [ ] **9.** Тест из PAS (баг-репорт + ошибка сервера)
- [ ] **10.** `MANTIS_ENABLE_ADMIN=0`, бэкап volumes

---

## Этап 1 — Подготовка сервера

```bash
# SSH на сервер (см. taxi-prod-log-access.mdc)
ssh user@91.219.60.148

sudo mkdir -p /opt/mantis
sudo chown $USER:$USER /opt/mantis
cd /opt/mantis
```

Скопировать с dev-машины (из репозитория PAS_4):

```bash
scp deploy/mantis/docker-compose.yml user@91.219.60.148:/opt/mantis/
```

**Обязательно** заменить в `docker-compose.yml`:

- `MARIADB_ROOT_PASSWORD`
- `MARIADB_PASSWORD`

Порт `8989` слушает только `127.0.0.1` — с интернета доступ через nginx.

---

## Этап 2 — Запуск контейнеров

```bash
cd /opt/mantis
docker compose pull
docker compose up -d
docker compose ps
docker compose logs -f mantisbt   # дождаться старта Apache
```

Контейнеры:

| Сервис | Образ | Назначение |
|--------|-------|------------|
| `mantisbt` | xlrl/mantisbt:2.28.0 | PHP + Apache + MantisBT |
| `mantis-db` | mariadb:11.7 | БД `bugtracker` |

Официального Docker-образа Mantis **нет** — используется community `xlrl/mantisbt`.

---

## Этап 3 — Nginx + HTTPS (пример)

Поддомен: `bugs.taxieasy.ua` (замените на свой).

```nginx
server {
    listen 443 ssl http2;
    server_name bugs.taxieasy.ua;

    # ssl_certificate /etc/letsencrypt/live/bugs.taxieasy.ua/fullchain.pem;
    # ssl_certificate_key /etc/letsencrypt/live/bugs.taxieasy.ua/privkey.pem;

    client_max_body_size 12M;

    location / {
        proxy_pass http://127.0.0.1:8989;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
sudo certbot --nginx -d bugs.taxieasy.ua
sudo nginx -t && sudo systemctl reload nginx
```

Проверка: открыть `https://bugs.taxieasy.ua` в браузере.

---

## Этап 4 — Установка Mantis (веб-мастер)

1. Открыть `https://bugs.taxieasy.ua/admin/install.php`
2. Параметры БД:

| Поле | Значение |
|------|----------|
| Тип БД | MySQL/MySQLi |
| Hostname | `mantis-db` (имя сервиса в compose) |
| Database | `bugtracker` |
| Username | `mantisbt` |
| Password | из `MARIADB_PASSWORD` |
| Admin user | `root` |
| Admin password | из `MARIADB_ROOT_PASSWORD` |

3. Создать администратора Mantis (логин/пароль для входа в UI)
4. После успеха — в `docker-compose.yml` поставить `MANTIS_ENABLE_ADMIN: "0"` и `docker compose up -d`

---

## Этап 5 — REST API для PAS

1. Войти в Mantis как администратор
2. **Управление** → **Управление пользователями** → создать пользователя `pas-api` (или использовать admin)
3. Профиль пользователя → **API-токены** → создать токен
4. Записать токен (показывается один раз)

Проверка с сервера или ПК:

```bash
curl -s -H "Authorization: ВАШ_API_TOKEN" \
  "https://bugs.taxieasy.ua/api/rest/users/me"
```

---

## Этап 6 — Проект и категория

1. **Создать проект** (например `PAS` или отдельно на каждое приложение)
2. Запомнить **Project ID** (в URL или Manage Project)
3. Создать категорию **Android / Bug Report**, запомнить **Category ID**

Опционально: Custom fields для версии приложения, города, email.

---

## Этап 7 — Firebase (ключи для PAS)

Коллекция Firestore: `keys` → документ `mantis_key`:

```json
{
  "api_token": "токен_из_шага_5",
  "base_url": "https://bugs.taxieasy.ua",
  "project_id": 1,
  "category_id": 5
}
```

- `base_url` — **без** `/api/rest` и без слэша в конце
- После сохранения — перезапуск приложения не нужен (читается при отправке)

---

## Этап 8 — Проверка из PAS

1. Собрать/установить PAS с веткой Mantis+fallback
2. Меню → отправить баг-репорт:
   - **Mantis OK** → toast «Заявку #N створено», в Telegram ссылка
   - **Mantis нет** → toast «Відправлено в Telegram з логами», файл в Telegram
3. Вызвать ошибку сервера (`server_error_connected`) — то же поведение

Логи Android: тег `MantisBugReportSender` — строка `Mantis unavailable, fallback to legacy Telegram`.

---

## Этап 9 — Бэкап и обслуживание

```bash
# Список volumes
docker volume ls | grep mantis

# Бэкап БД
docker compose exec mantis-db mariadb-dump -u mantisbt -p bugtracker > backup-$(date +%F).sql

# Обновление образа (осторожно, с бэкапом)
cd /opt/mantis
docker compose pull
docker compose up -d
```

Volumes для бэкапа: `mantis-db-data`, `mantis-config`, `mantis-upload`.

---

## Продолжение на другом ПК

1. `git pull` в **PAS_4** (и PAS_1–3 при релизе)
2. Открыть этот файл: `docs/mantis-docker-deploy.md`
3. Отметить чеклист выше
4. SSH на сервер → `cd /opt/mantis && docker compose ps`
5. Если контейнеры не поднимали — с **Этапа 1**
6. Если Mantis уже стоит — с **Этапа 5** (API) или **Этапа 7** (Firebase)

Файлы в репозитории:

| Путь | Назначение |
|------|------------|
| `deploy/mantis/docker-compose.yml` | Compose для сервера |
| `docs/mantis-docker-deploy.md` | Этот план |
| `app/src/.../mantis/MantisBugReportSender.java` | Mantis + fallback |

---

## Безопасность

- API-токен только в Firebase, не в APK
- `MANTIS_ENABLE_ADMIN=0` после установки
- HTTPS обязателен
- Ограничить доступ к UI Mantis по IP/VPN при необходимости
- Регулярный бэкап `mantis-db-data`
