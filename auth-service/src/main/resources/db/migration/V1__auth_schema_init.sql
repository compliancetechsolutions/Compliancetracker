-- ============================================================
-- V1__auth_schema_init.sql
-- Auth schema — initial structure
-- Run by Flyway on auth-service startup
-- ============================================================

CREATE SCHEMA IF NOT EXISTS auth_schema;

-- ── Users ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS auth_schema.users (
    user_id       UUID         NOT NULL DEFAULT gen_random_uuid(),
    username      VARCHAR(100) NOT NULL,
    email         VARCHAR(200) NOT NULL,
    password_hash TEXT         NOT NULL,
    first_name    VARCHAR(100),
    last_name     VARCHAR(100),
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    -- Audit
    version       BIGINT       NOT NULL DEFAULT 0,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),
    is_deleted    BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_users        PRIMARY KEY (user_id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email    UNIQUE (email)
);

CREATE INDEX idx_users_username ON auth_schema.users (username) WHERE is_deleted = FALSE;
CREATE INDEX idx_users_email    ON auth_schema.users (email)    WHERE is_deleted = FALSE;
CREATE INDEX idx_users_status   ON auth_schema.users (status)   WHERE is_deleted = FALSE;

-- ── Roles ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS auth_schema.roles (
    role_id     UUID         NOT NULL DEFAULT gen_random_uuid(),
    role_name   VARCHAR(100) NOT NULL,
    description TEXT,
    version     BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_roles      PRIMARY KEY (role_id),
    CONSTRAINT uq_role_name  UNIQUE (role_name)
);

-- ── Permissions ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS auth_schema.permissions (
    permission_id   UUID         NOT NULL DEFAULT gen_random_uuid(),
    permission_name VARCHAR(200) NOT NULL,
    description     TEXT,
    version         BIGINT       NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    is_deleted      BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_permissions     PRIMARY KEY (permission_id),
    CONSTRAINT uq_permission_name UNIQUE (permission_name)
);

-- ── User Roles ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS auth_schema.user_roles (
    user_role_id  UUID      NOT NULL DEFAULT gen_random_uuid(),
    user_id       UUID      NOT NULL REFERENCES auth_schema.users(user_id)       ON DELETE CASCADE,
    role_id       UUID      NOT NULL REFERENCES auth_schema.roles(role_id)       ON DELETE CASCADE,
    assigned_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    version       BIGINT    NOT NULL DEFAULT 0,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),
    is_deleted    BOOLEAN   NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_user_roles         PRIMARY KEY (user_role_id),
    CONSTRAINT uq_user_role          UNIQUE (user_id, role_id)
);

-- ── Role Permissions ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS auth_schema.role_permissions (
    role_permission_id UUID NOT NULL DEFAULT gen_random_uuid(),
    role_id            UUID NOT NULL REFERENCES auth_schema.roles(role_id)             ON DELETE CASCADE,
    permission_id      UUID NOT NULL REFERENCES auth_schema.permissions(permission_id) ON DELETE CASCADE,
    version            BIGINT   NOT NULL DEFAULT 0,
    created_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by         VARCHAR(100),
    updated_by         VARCHAR(100),
    is_deleted         BOOLEAN  NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_role_permissions PRIMARY KEY (role_permission_id),
    CONSTRAINT uq_role_permission  UNIQUE (role_id, permission_id)
);

-- ── Refresh Tokens ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS auth_schema.refresh_tokens (
    token_id    UUID      NOT NULL DEFAULT gen_random_uuid(),
    user_id     UUID      NOT NULL REFERENCES auth_schema.users(user_id) ON DELETE CASCADE,
    token       TEXT      NOT NULL,        -- SHA-256 hash of raw token
    expiry_time TIMESTAMP NOT NULL,
    revoked     BOOLEAN   NOT NULL DEFAULT FALSE,
    version     BIGINT    NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    is_deleted  BOOLEAN   NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_refresh_tokens PRIMARY KEY (token_id)
);

CREATE INDEX idx_refresh_tokens_user_id ON auth_schema.refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_token   ON auth_schema.refresh_tokens (token)   WHERE revoked = FALSE;

-- ── Login History ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS auth_schema.login_history (
    login_id    UUID      NOT NULL DEFAULT gen_random_uuid(),
    user_id     UUID,                      -- nullable: failed logins may have no user
    login_time  TIMESTAMP NOT NULL DEFAULT NOW(),
    ip_address  VARCHAR(100),
    success     BOOLEAN   NOT NULL DEFAULT FALSE,
    version     BIGINT    NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    is_deleted  BOOLEAN   NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_login_history PRIMARY KEY (login_id)
);

CREATE INDEX idx_login_history_user_id ON auth_schema.login_history (user_id);
CREATE INDEX idx_login_history_time    ON auth_schema.login_history (login_time DESC);

-- ── User Sessions ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS auth_schema.user_sessions (
    session_id    UUID      NOT NULL DEFAULT gen_random_uuid(),
    user_id       UUID      NOT NULL REFERENCES auth_schema.users(user_id) ON DELETE CASCADE,
    jwt_token     TEXT,
    session_start TIMESTAMP NOT NULL DEFAULT NOW(),
    session_end   TIMESTAMP,
    active        BOOLEAN   NOT NULL DEFAULT TRUE,
    version       BIGINT    NOT NULL DEFAULT 0,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),
    is_deleted    BOOLEAN   NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_user_sessions PRIMARY KEY (session_id)
);

CREATE INDEX idx_user_sessions_user_id ON auth_schema.user_sessions (user_id) WHERE active = TRUE;

-- ── Seed data ─────────────────────────────────────────────
-- Default roles (bcrypt hashed admin password inserted in V2__seed_data.sql)
INSERT INTO auth_schema.roles (role_id, role_name, description)
VALUES
    (gen_random_uuid(), 'ADMIN',                  'Platform administrator'),
    (gen_random_uuid(), 'INVESTOR',               'Investor / limited partner'),
    (gen_random_uuid(), 'COMPANY_REPRESENTATIVE', 'Entity compliance representative')
ON CONFLICT (role_name) DO NOTHING;
