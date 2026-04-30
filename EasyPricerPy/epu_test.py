from generated.tables import *
import json
import requests


if __name__ == '__main__':

    api_url = "http://localhost:8080/daycount/r03/NASD_30_360"
    response = requests.get(api_url)
    daycount = Daycount()
    daycount.fromJson(**response.json())  
    print(daycount.getDescription())
