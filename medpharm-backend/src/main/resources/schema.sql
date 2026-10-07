DROP TABLE IF EXISTS detalles_receta;
DROP TABLE IF EXISTS recetas;
DROP TABLE IF EXISTS medicamentos;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(100) NOT NULL,
    nombre_completo VARCHAR(120) NOT NULL,
    rol             VARCHAR(20)  NOT NULL
);

CREATE TABLE medicamentos (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo          VARCHAR(20)   NOT NULL UNIQUE,
    nombre          VARCHAR(120)  NOT NULL,
    stock           INT           NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL
);

CREATE TABLE recetas (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_receta   VARCHAR(30)  NOT NULL UNIQUE,
    paciente_nombre VARCHAR(120) NOT NULL,
    medico_id       BIGINT       NOT NULL,
    estado          VARCHAR(20)  NOT NULL,
    fecha_emision   TIMESTAMP    NOT NULL,
    CONSTRAINT fk_receta_medico FOREIGN KEY (medico_id) REFERENCES usuarios(id)
);

CREATE TABLE detalles_receta (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    receta_id      BIGINT       NOT NULL,
    medicamento_id BIGINT       NOT NULL,
    cantidad       INT          NOT NULL,
    dosis_indicada VARCHAR(200),
    CONSTRAINT fk_detalle_receta FOREIGN KEY (receta_id) REFERENCES recetas(id),
    CONSTRAINT fk_detalle_medicamento FOREIGN KEY (medicamento_id) REFERENCES medicamentos(id)
);
