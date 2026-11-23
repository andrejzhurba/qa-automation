# QA automation

UI tests for [SauceDemo](https://www.saucedemo.com/) and API tests for
[Restful Booker](https://restful-booker.herokuapp.com/apidoc/index.html).

Stack: Java 17, JUnit 5, Selenide, REST Assured, Maven.

## Prerequisites

For local execution: JDK 17+, Maven 3.9+ and Chrome. API-only runs do not require Chrome.
Selenium Manager downloads the matching ChromeDriver on the first local UI run.

For Docker execution: Docker with Linux containers enabled. Java, Maven, Chrome and
ChromeDriver are included in the image. The image targets `linux/amd64`.

Both options require internet access to the demo services. The first build also downloads
test dependencies.

## Local execution

Run all tests:

```bash
mvn test
```

Run UI tests:

```bash
mvn test "-Dgroups=ui"
```

Run API tests:

```bash
mvn test "-Dgroups=api"
```

UI tests run headless by default. To see Chrome:

```bash
mvn test "-Dgroups=ui" "-Dselenide.headless=false"
```

To run one scenario:

```bash
mvn test "-Dtest=ProductAndCartTest#cheapestProductCanBeAddedToCart" "-Dselenide.headless=false"
```

Quoted JVM arguments also work in PowerShell.

## Docker execution

From the project directory:

```bash
docker build -t qa-automation-tests .
docker run --rm qa-automation-tests
```

Run an individual suite:

```bash
docker run --rm qa-automation-tests mvn -B test "-Dgroups=ui"
docker run --rm qa-automation-tests mvn -B test "-Dgroups=api"
```

The image pins Maven 3.9.11 and Chrome for Testing 158.0.8127.36 with the same ChromeDriver
version. Update the `CHROME_VERSION` build argument to upgrade both browser and driver.
On ARM hosts, add `--platform=linux/amd64` to both Docker commands.

On Linux with X11/XWayland, `./scripts/run-ui-visible.sh` shows the browser on the desktop.
The standard Docker run uses headless Chrome.

## Configuration

Override settings with JVM properties, for example:

```bash
mvn test "-Dgroups=api" "-Dapi.baseUrl=http://localhost:3001"
```

| Property | Default |
| --- | --- |
| `ui.baseUrl` | `https://www.saucedemo.com/` |
| `ui.username` | `standard_user` |
| `ui.password` | `secret_sauce` |
| `api.baseUrl` | `https://restful-booker.herokuapp.com` |
| `api.username` | `admin` |
| `api.password` | `password123` |
| `api.timeout` | `10000` ms for connection and response |
| `selenide.timeout` | `8000` ms for UI conditions |

The default credentials are provided by the demo services.

## Test approach

- UI tests describe the scenarios; page objects hold selectors, actions and page assertions.
  Selectors use SauceDemo's `data-test` attributes, which identify elements independently of styling.
- Selenide's `shouldBe` and `shouldHave` conditions wait for visibility, text and collection contents.
  Sorting checks wait for the full expected order. Tests use no fixed sleeps.
- API requests are kept in `BookingApi`; `BookingTest` checks statuses, a positive booking ID
  and response data. Java records represent bookings and dates. Record equality checks every
  field in both the creation response and the subsequent GET response.
- Each UI test starts a fresh browser session. API tests use unique guest names and delete
  their bookings in `@AfterEach`. Shared URLs, credentials and timeouts are in `Config`.

## Coverage and limitations

- UI: successful and blocked login, sorting by name and price in both directions,
  adding the cheapest product to the cart, removing a product.
- API: create, retrieve, update and delete a booking; unknown ID, missing required
  fields and invalid token.

Catalog checks assume the six SauceDemo products and dollar prices. Restful Booker returns
500 when required booking fields are missing; the negative test checks this response.
The public services can be unavailable or change their behavior, causing tests to fail.

## Reports

JUnit reports: `target/surefire-reports`.
UI failure screenshots and page sources: `target/selenide`.

To save reports from Docker:

```bash
docker run --rm -v "$PWD/target:/project/target" qa-automation-tests
```
