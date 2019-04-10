FROM postgres:11-alpine

ENV POSTGRES_USER sakila
ENV POSTGRES_PASSWORD sakila
ENV POSTGRES_DB sakila

COPY sql/sakila.sql /docker-entrypoint-initdb.d/
