FROM nexus.tmbbank.local:60021/mountebank:2.3.0

EXPOSE 8080
EXPOSE 2525

WORKDIR /app/
COPY templates/ ./templates/
COPY entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh

ENTRYPOINT /app/entrypoint.sh
