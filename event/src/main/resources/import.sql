CREATE TABLE IF NOT EXISTS public.byte_events (
                                           id UUID NOT NULL,
                                           created_at TIMESTAMP(6),
                                           event OID NOT NULL,
                                           type_identifier SMALLINT NOT NULL,
                                           PRIMARY KEY (id)
);