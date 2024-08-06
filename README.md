# Weather App

## How to run the app
1. Clone the repository
2. Run `./gradlew bootRun` in the root directory of the project
3. Run `curl --location 'http://localhost:8080/weather/au/melbourne' \
   --header 'X-API-KEY: key2'`

## Whats done
1. The app is able to fetch weather data from the OpenWeatherMap API
2. It calls the OpenWeatherMap API with the city and country code
3. It uses the API key from the header to authenticate the request (currently hard coded keys)
4. It returns the weather data in the response
5. The app is able to run tests
6. Rate limiting is implemented 5 requests per hour for every api keys (currently hard coded keys)
7. The api fetches the weather data from the OpenWeatherMap API only if the data is not present in the H2 database

## Whats can be improved
1. Error handling in details
2. Logging
3. API key management
4. more unit testing



