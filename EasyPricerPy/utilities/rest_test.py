import requests

url = "https://api.investing.com/api/financialdata/1080334/historical/chart/"
query_params = {"interval=PT15M&pointscount=160"}

headers = {"User-Agent": "Mozilla/5.0"}

# La chiamata diventa .get() e usiamo 'params' invece di 'json'
response = requests.get(url, params=query_params, headers=headers)

print(response.url) # Vedrai: .../getIndexPerformance/?isin=DE000SL0FHR2
print(response.json())