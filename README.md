# Pogodynka
 
Android Weather app that uses "Current weather data" API from openweathermap.org to show current weather for given city.

## Start page
Starting the application, opens the Start page (content view "activity_start"), which contains:
- EditText widget where city name should be given;
- Button that:
 - gets text from the EditText wiget to then get information about weather in given city from the API
 - sets "activity_weather" as content view to show weather data
![Start page](screenshots/Start.png)

## Weather page
After successfully using given city to get current weather data, the data is shown on Weather page (content view "activity_wether"). 
Data that is shown:
- City name that was given
- Temperature
- Weather condition (rainny, snowy, clear sky etc.) in form of description on the side and icon beside city name
- Current date and time (used as well to determine time of the day)
- Temperature parameter accounts for the human perception of weather
- Minimum temperature
- Maximum temperature
- Wind speed and direction
- Rain valume for the last hour

![Start page](screenshots/London.png)
