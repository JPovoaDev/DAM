import urllib.request
import json

def fetch(url):
    print(f"URL: {url}")
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read().decode())
            data_str = str(data)
            print(f"Data snippet: {data_str:.200s}...\n")
    except Exception as e:
        print(f"Error: {e}\n")

print("--- Player search p=Ronaldo ---")
fetch("https://www.thesportsdb.com/api/v1/json/3/searchplayers.php?p=Ronaldo")

print("--- Player search p= (Empty) ---")
fetch("https://www.thesportsdb.com/api/v1/json/3/searchplayers.php?p=")

print("--- Team search t=Real Madrid ---")
fetch("https://www.thesportsdb.com/api/v1/json/3/searchteams.php?t=Real%20Madrid")

print("--- Team search t= (Empty) ---")
fetch("https://www.thesportsdb.com/api/v1/json/3/searchteams.php?t=")

print("--- Search all teams in a league (Default data option) l=English%20Premier%20League ---")
fetch("https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=English%20Premier%20League")
