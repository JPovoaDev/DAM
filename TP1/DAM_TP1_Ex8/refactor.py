import os
import re
import shutil
from pathlib import Path

base_dir = r'C:\Users\olavo\OneDrive\Ambiente de Trabalho\ISEL\S6-ISEL\DAM\DAM_TP1_Ex8\app\src\main\java\com\example\footballscout'
base_path = Path(base_dir)

moves = []

# Collect files and determine new package
for root, dirs, files in os.walk(base_dir):
    for f in files:
        if not f.endswith('.kt') and not f.endswith('.java'): continue
        old_path = Path(root) / f
        rel_path = old_path.relative_to(base_path)
        
        # Current package
        dir_parts = list(rel_path.parent.parts)
        old_pkg = 'com.example.footballscout'
        if dir_parts:
            old_pkg += '.' + '.'.join(dir_parts)

        new_pkg = old_pkg
        
        # Apply rules
        if new_pkg.startswith('com.example.footballscout.domain.model'):
            new_pkg = new_pkg.replace('domain.model', 'data.model')
        elif new_pkg.startswith('com.example.footballscout.data.local'):
            new_pkg = new_pkg.replace('data.local', 'data.database')
        elif 'Adapter' in f and new_pkg.startswith('com.example.footballscout.ui'):
            new_pkg = 'com.example.footballscout.ui.adapters'
        elif 'ViewModel' in f and new_pkg.startswith('com.example.footballscout.ui'):
            new_pkg = 'com.example.footballscout.viewmodel'
        elif f == 'MainActivity.kt' and new_pkg == 'com.example.footballscout':
            new_pkg = 'com.example.footballscout.ui.activities'
        elif 'Fragment' in f and new_pkg.startswith('com.example.footballscout.ui'):
            new_pkg = 'com.example.footballscout.ui.activities'

        if new_pkg != old_pkg:
            new_rel_dir = new_pkg.replace('com.example.footballscout.', '').replace('com.example.footballscout', '').replace('.', '/')
            new_rel_dir = new_rel_dir.lstrip('/')
            new_path = base_path / new_rel_dir / f
            moves.append({
                'old_path': old_path,
                'new_path': new_path,
                'old_pkg_full': old_pkg + '.' + f.replace('.kt', ''),
                'new_pkg_full': new_pkg + '.' + f.replace('.kt', ''),
                'old_pkg': old_pkg,
                'new_pkg': new_pkg
            })

print(f'Found {len(moves)} files to move.')
for m in moves:
    print(f"{m['old_pkg_full']} -> {m['new_pkg_full']}")

# Execute moves
for m in moves:
    new_path = Path(m['new_path'])
    new_path.parent.mkdir(parents=True, exist_ok=True)
    shutil.move(str(m['old_path']), str(new_path))

# Delete old empty directories
for root, dirs, files in os.walk(base_dir, topdown=False):
    for d in dirs:
        dir_path = Path(root) / d
        try:
            dir_path.rmdir()
        except OSError:
            pass # Not empty

# Replace imports in all .kt files (and layouts)
all_files_to_update = []
for root, dirs, files in os.walk(r'C:\Users\olavo\OneDrive\Ambiente de Trabalho\ISEL\S6-ISEL\DAM\DAM_TP1_Ex8\app\src\main'):
    for f in files:
        if f.endswith('.kt') or f.endswith('.xml'):
            all_files_to_update.append(Path(root) / f)

for file_path in all_files_to_update:
    try:
        content = file_path.read_text(encoding='utf-8')
    except Exception:
        continue
    
    new_content = content
    # Update package declarations
    if file_path.suffix == '.kt':
        for m in moves:
            if file_path == m['new_path']:
                new_content = re.sub(r'^package ' + re.escape(m['old_pkg']) + r'\b', 'package ' + m['new_pkg'], new_content, flags=re.MULTILINE)
    
    # Update imports and fully qualified names (like in XML)
    for m in moves:
        new_content = re.sub(r'\b' + re.escape(m['old_pkg_full']) + r'\b', m['new_pkg_full'], new_content)
        # also replace exact package imports
        new_content = re.sub(r'\b' + re.escape(m['old_pkg']) + r'\.' + r'\*', m['new_pkg'] + '.*', new_content)

    if new_content != content:
        file_path.write_text(new_content, encoding='utf-8')
        print(f"Updated {file_path.name}")
