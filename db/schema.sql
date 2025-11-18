-- Optional: drop old tables (child tables first because of FKs)
DROP TABLE IF EXISTS "bill" CASCADE;

DROP TABLE IF EXISTS "transaction" CASCADE;

DROP TABLE IF EXISTS "appointment" CASCADE;

DROP TABLE IF EXISTS "user" CASCADE;

-- USER
CREATE TABLE "user" (
    "id" SERIAL PRIMARY KEY,
    "balance" INTEGER DEFAULT 0,
    "gioitinh" VARCHAR(50),
    "hoten" VARCHAR(100),
    "mabn" VARCHAR(20),
    "mapin" VARCHAR(100),
    "ngaysinh" VARCHAR(50),
    "publicKey" VARCHAR(255),
    "quequan" VARCHAR(100),
    "sdt" VARCHAR(50)
);

-- APPOINTMENT
CREATE TABLE "appointment" (
    "id" SERIAL PRIMARY KEY,
    "cost" INTEGER,
    "date" VARCHAR(50),
    "description" VARCHAR(255),
    "name" VARCHAR(100),
    "patientId" INTEGER NOT NULL,
    -- In MySQL you had BIT(1). In Postgres BOOLEAN is more natural:
    "status" BOOLEAN,
    "code" VARCHAR(50),
    CONSTRAINT "fk_appointment_patient" FOREIGN KEY ("patientId") REFERENCES "user"("id") ON UPDATE CASCADE ON DELETE RESTRICT
);

-- Index for appointment.patientId
CREATE INDEX "idx_appointment_patient" ON "appointment" ("patientId");

-- BILL
CREATE TABLE "bill" (
    "id" SERIAL PRIMARY KEY,
    "paymentDate" VARCHAR(50),
    "cost" INTEGER,
    "patientId" INTEGER NOT NULL,
    "patientName" VARCHAR(50),
    "appointmentId" INTEGER NOT NULL,
    "code" VARCHAR(50),
    CONSTRAINT "fk_bill_patient" FOREIGN KEY ("patientId") REFERENCES "user"("id") ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT "fk_bill_appointment" FOREIGN KEY ("appointmentId") REFERENCES "appointment"("id") ON UPDATE CASCADE ON DELETE RESTRICT
);

-- Indexes for bill
CREATE INDEX "idx_bill_patient" ON "bill" ("patientId");

CREATE INDEX "idx_bill_appointment" ON "bill" ("appointmentId");

-- TRANSACTION
CREATE TABLE "transaction" (
    "id" SERIAL PRIMARY KEY,
    "cost" INTEGER,
    "date" VARCHAR(50),
    "patientId" INTEGER NOT NULL,
    "patientName" VARCHAR(100),
    "type" VARCHAR(50),
    CONSTRAINT "fk_transaction_patient" FOREIGN KEY ("patientId") REFERENCES "user"("id") ON UPDATE CASCADE ON DELETE RESTRICT
);

-- Index for transaction.patientId
CREATE INDEX "idx_transaction_patient" ON "transaction" ("patientId");
