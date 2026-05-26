#!/usr/bin/env python3
"""Pass 11: Fix ResourceKey.of() -> ResourceKey.create() and other remaining issues."""

import os
import re

SRC = os.path.join(os.path.dirname(__file__), "src", "main", "java")

CODE_REPLACEMENTS = [
    # ResourceKey.of(registryKey, identifier) -> ResourceKey.create(registryKey, identifier)
    (r'ResourceKey\.of\(', 'ResourceKey.create('),

    # Fix CraftingInput.of(1, 1, java.util.List.of(stack) syntax error (missing closing paren)
    # Pattern: CraftingInput.of(1, 1, java.util.List.of(X)) where the outer paren is missing
    # This was introduced by pass 10's regex - it replaced "new CraftingInput(" with
    # "CraftingInput.of(1, 1, java.util.List.of(" but didn't add the closing paren
    # The result was: CraftingInput.of(1, 1, java.util.List.of(this.getItem(INPUT_SLOT));
    # Should be:      CraftingInput.of(1, 1, java.util.List.of(this.getItem(INPUT_SLOT)));
    # Fix: find the pattern and add closing paren before semicolon
    (r'CraftingInput\.of\(1, 1, java\.util\.List\.of\(([^)]+)\);', r'CraftingInput.of(1, 1, java.util.List.of(\1));'),
]


def process_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        original = f.read()

    content = original
    for pattern, replacement in CODE_REPLACEMENTS:
        content = re.sub(pattern, replacement, content)

    if content != original:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False


def main():
    updated = 0
    total = 0
    for root, dirs, files in os.walk(SRC):
        dirs[:] = [d for d in dirs if d != '__pycache__']
        for fname in files:
            if not fname.endswith('.java'):
                continue
            path = os.path.join(root, fname)
            total += 1
            if process_file(path):
                updated += 1
                print(f"  Updated: {os.path.relpath(path, SRC)}")

    print(f"\nPass 11 complete: {updated}/{total} files updated.")


if __name__ == '__main__':
    main()
