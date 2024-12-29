CREATE TABLE user_ (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100)
);

CREATE TABLE appointment (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    CONSTRAINT fk_patient
        FOREIGN KEY(patient_id)
            REFERENCES user_(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_doctor
        FOREIGN KEY(doctor_id)
            REFERENCES user_(id)
            ON DELETE CASCADE
);

CREATE TABLE medical_record (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    record_date DATE NOT NULL,
    diagnosis TEXT,
    treatment TEXT,
    prescription TEXT,
    CONSTRAINT fk_medical_record_patient
        FOREIGN KEY(patient_id)
            REFERENCES user_(id)
            ON DELETE CASCADE
);
