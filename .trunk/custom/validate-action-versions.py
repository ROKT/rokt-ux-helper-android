#!/usr/bin/env python3

import sys
import re
import yaml
from typing import List, Dict, Any

def load_workflow_file(file_path: str) -> Dict[str, Any]:
    """Load and parse a GitHub Actions workflow file."""
    try:
        with open(file_path, 'r') as f:
            return yaml.safe_load(f)
    except Exception as e:
        print(f"Error loading workflow file: {e}")
        sys.exit(1)

def validate_action_version(uses: str) -> bool:
    """
    Validate that a GitHub Action is pinned to a specific commit SHA.
    Returns True if valid, False otherwise.
    """
    # Skip local actions (they start with .)
    if uses.startswith('.'):
        return True
        
    # Skip actions from ROKT/rokt-workflows
    if uses.startswith('ROKT/rokt-workflows/'):
        return True
        
    # Check if it's a GitHub action (contains @)
    if '@' not in uses:
        return False
        
    # Split into action and version
    action, version = uses.rsplit('@', 1)
    
    # Check if version is a commit SHA (40 character hex string)
    if not re.match(r'^[0-9a-f]{40}$', version):
        return False
        
    return True

def find_actions_in_steps(steps: List[Dict[str, Any]]) -> List[str]:
    """Extract all 'uses' fields from steps."""
    actions = []
    for step in steps:
        if 'uses' in step:
            actions.append(step['uses'])
    return actions

def validate_workflow(workflow: Dict[str, Any]) -> bool:
    """Validate all actions in a workflow file."""
    is_valid = True
    
    # Check jobs
    for job_name, job in workflow.get('jobs', {}).items():
        # Check if job directly uses a reusable workflow
        if 'uses' in job:
            if not validate_action_version(job['uses']):
                print(f"Error: Job '{job_name}' uses workflow '{job['uses']}' which is not pinned to a commit SHA")
                is_valid = False
            continue

        # Check steps in job if present
        steps = job.get('steps', [])
        actions = find_actions_in_steps(steps)
        
        for action in actions:
            if not validate_action_version(action):
                print(f"Error: Action '{action}' is not pinned to a commit SHA")
                is_valid = False
    
    return is_valid

def main():
    if len(sys.argv) != 2:
        print("Usage: validate-action-versions.py <workflow-file-path>")
        sys.exit(1)
        
    workflow_path = sys.argv[1]
    workflow = load_workflow_file(workflow_path)
    
    if validate_workflow(workflow):
        sys.exit(0)
    else:
        sys.exit(1)

if __name__ == "__main__":
    main() 