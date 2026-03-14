import urllib.request
import json

def fetch(url):
    print(f"\n--- Fetching {url} ---")
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read().decode())
            print(f"Keys: {list(data.keys()) if isinstance(data, dict) else 'Not a dict'}")
            if isinstance(data, dict):
                # Print sample of the first array item if it exists
                for key, val in data.items():
                    if val and isinstance(val, list) and len(val) > 0:
                        item = val[0]
                        if isinstance(item, dict):
                            print(f"First item keys in {key}: {list(item.keys())}")
                            # Look for description or career or goals
                            for k, v in item.items():
                                if 'strDescription' in k or 'Goal' in k or 'Team' in k or 'Career' in k:
                                    if v:
                                        # Use string formatting to truncate and avoid Pyright slicing errors
                                        v_str = str(v)
                                        v_str_trunc = "{:.100s}".format(v_str)
                                        suffix = "..." if len(v_str) > 100 else ""
                                        # Avoid Windows console charmap encoding errors by replacing unencodable chars
                                        print(f"  {k}: {v_str_trunc}{suffix}".encode('ascii', 'replace').decode('ascii'))
                if not any(data.values()):
                    print("Result is empty.")
    except Exception as e:
        print(f"Error: {e}")

# Player description
fetch("https://www.thesportsdb.com/api/v1/json/3/lookupplayer.php?id=34146304")

# Former teams
fetch("https://www.thesportsdb.com/api/v1/json/3/lookupformerteams.php?id=34146304")

# Player honors/milestones
fetch("https://www.thesportsdb.com/api/v1/json/3/lookuphonors.php?id=34146304")
fetch("https://www.thesportsdb.com/api/v1/json/3/lookupmilestones.php?id=34146304")

# All teams in a league
fetch("https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=English%20Premier%20League")
