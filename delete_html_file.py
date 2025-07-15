import os
from pathlib import Path

file_to_delete = Path('C:/Users/dongA/AndroidStudioProjects/wiki_app/app/src/main/assets/posts/UG/goat/Major Goat Diseases_Prevention and Treatment.html')

if file_to_delete.exists():
    file_to_delete.unlink()
    print(f"파일 삭제 완료: {file_to_delete}")
else:
    print(f"파일이 존재하지 않습니다: {file_to_delete}")
