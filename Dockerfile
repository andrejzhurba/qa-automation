FROM maven:3.9.11-eclipse-temurin-17-noble@sha256:e4a7ace3dc0d645ed97f8d9ad0b0d3f0b14fa8d150138f27f116d7105a639b82

ARG CHROME_VERSION=158.0.8127.36

RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      ca-certificates curl unzip fonts-liberation \
      libasound2t64 libatk-bridge2.0-0t64 libcups2t64 libgbm1 libgtk-3-0t64 \
      libnss3 libx11-xcb1 libxcomposite1 libxdamage1 libxfixes3 libxkbcommon0 libxrandr2 \
 && curl -fsSL --retry 3 -o /tmp/chrome.zip \
      "https://storage.googleapis.com/chrome-for-testing-public/${CHROME_VERSION}/linux64/chrome-linux64.zip" \
 && unzip -q /tmp/chrome.zip -d /opt \
 && curl -fsSL --retry 3 -o /tmp/chromedriver.zip \
      "https://storage.googleapis.com/chrome-for-testing-public/${CHROME_VERSION}/linux64/chromedriver-linux64.zip" \
 && unzip -q /tmp/chromedriver.zip -d /tmp \
 && install -m 755 /tmp/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver \
 && /opt/chrome-linux64/chrome --version \
 && chromedriver --version \
 && rm /tmp/chrome.zip /tmp/chromedriver.zip \
 && rm -rf /tmp/chromedriver-linux64 /var/lib/apt/lists/*

WORKDIR /project
ENV QA_CONTAINER=true \
    JAVA_TOOL_OPTIONS="-Dselenide.browserBinary=/opt/chrome-linux64/chrome -Dwebdriver.chrome.driver=/usr/local/bin/chromedriver"

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src

CMD ["mvn", "-B", "test"]
