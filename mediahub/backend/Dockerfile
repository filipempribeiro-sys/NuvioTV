FROM node:20-alpine
WORKDIR /app
COPY package.json ./
COPY src ./src
COPY test ./test
ENV NODE_ENV=production
EXPOSE 8080
CMD ["node", "src/server.js"]
