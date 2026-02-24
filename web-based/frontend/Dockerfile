# Build frontend static files
FROM node:22-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# Tiny image that holds the built files and copies them to shared volume
FROM alpine:3.20
COPY --from=builder /app/dist /dist
# On start: copy built files to the shared volume, then exit
CMD ["sh", "-c", "cp -r /dist/. /output/ && echo '[frontend] Build copied to volume'"]
