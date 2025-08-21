import json
import sys

print(json.dumps(json.loads(sys.argv[1]), indent=2))
