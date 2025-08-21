import json
import re
import sys

with open(sys.argv[1], "r") as f:
    data = json.load(f)

minified = json.dumps(json.dumps(data, separators=(",", ":")))

with open(sys.argv[2], "r") as f:
    data = f.read()

if len(sys.argv) == 4 and sys.argv[3] == "i":
    type = '"layoutVariantSchema": '
    trailer = ""
else:
    type = '"outerLayoutSchema": '
    trailer = ","

data = re.sub(f"(?<={type}).*?(?=\n)", minified.replace("\\", "\\\\") + trailer, data)

with open(sys.argv[2], "w") as f:
    f.write(data)
