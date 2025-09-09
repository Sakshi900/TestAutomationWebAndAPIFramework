# Selenium + TestNG + POM + Allure (Web + API)

### Run locally
```bash
# Run tests (ensure JDK 23+ installed)
mvn clean test

### Run with System properties
mvn clean test -Denvironment=qa -Dbrowser=firefox -Dheadless=true

```

### Allure reporting

#### Option 1 – Serve directly
```bash
allure serve target/allure-results
```
➡ Generates the report and opens it in your default browser.

#### Option 2 – Generate + open manually
```bash
allure generate target/allure-results -o target/allure-report --clean
allure open target/allure-report
```

### Environment overrides
```bash
mvn -Dbrowser=firefox -Dheadless=true test
```

### CI
GitHub Actions workflow provided in `.github/workflows/ci.yml` uploads Allure results as artifacts.
