# Rokt Android SDK development tools

Collection of scripts to format JSON that may be useful for testing DCUI layouts.

Requirements: `python3`

## pretty.py

[pretty.py](pretty.py) prettifies a minified JSON string that is in the format used by outerLayoutSchema and layoutVariantSchema.

Usage: `python3 pretty.py your_mini_json_string_here`

Takes a minified JSON string as a sys arg and outputs to stdout.

Pipe it into pbcopy for ease of use. `python3 pretty.py your_mini_json_string_here | pbcopy`

Remember to add quotes to your json string.

## copy_to.py

[copy_to.py](copy_to.py) minifies and copies the content from a pretty JSON file into a schema in a JSON file representing an experience API response. Use the `i` flag as the last arg to copy the content to `layoutVariantSchema` and no flag to copy to `outerLayoutSchema`. This lets you write the outer layout and inner layout json in two separate pretty files and easily copy them into a response for testing them.

_Note:_ the `experience_response_file` should already exist and be correctly formatted, this script just updates the desired minified json. If there are multiple innerlayouts, it will update all of them.

Usage: `python3 copy_to.py pretty_schema_input_file experience_response_file i`

## apply_dcui.sh

[apply_dcui.sh](apply_dcui.sh) is a helper script that merges inner and outer layouts, installs the Playground application and starts the Playground application to see the experience payload.
