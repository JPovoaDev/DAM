import urllib.request
import json

def fetch(url):
    print(f"URL: {url}")
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read().decode())
            data_str = str(data)
            print(f"Data: {data_str:.300s}...\n")
    except Exception as e:
        print(f"Error: {e}\n")

print("--- Lookup Player ---")
fetch("https://www.thesportsdb.com/api/v1/json/3/lookupplayer.php?id=34146304")

print("--- Team Search ---")
fetch("https://www.thesportsdb.com/api/v1/json/3/searchteams.php?t=Madrid")
fetch("https://www.thesportsdb.com/api/v1/json/3/searchteams.php?t=Manchester")
