import os
import markdown
import re
import requests
from pathlib import Path
from urllib.parse import urlparse
import hashlib

def download_image(url, output_dir):
    try:
        # URL에서 파일 확장자 추출
        parsed_url = urlparse(url)
        file_ext = os.path.splitext(parsed_url.path)[1]
        if not file_ext:
            file_ext = '.jpg'  # 기본 확장자
        
        # URL을 해시하여 고유한 파일명 생성
        url_hash = hashlib.md5(url.encode()).hexdigest()
        filename = f"{url_hash}{file_ext}"
        output_path = os.path.join(output_dir, filename)
        
        # 이미지가 이미 존재하면 다운로드 건너뛰기
        if os.path.exists(output_path):
            return filename
        
        # 이미지 다운로드
        response = requests.get(url, stream=True)
        response.raise_for_status()
        
        # 이미지 저장
        with open(output_path, 'wb') as f:
            for chunk in response.iter_content(chunk_size=8192):
                f.write(chunk)
        
        return filename
    except Exception as e:
        print(f"이미지 다운로드 실패 ({url}): {str(e)}")
        return None

def process_images(html_content, output_dir):
    # 이미지 태그 찾기
    img_pattern = r'<img[^>]+src="([^"]+)"'
    
    def replace_img(match):
        full_tag = match.group(0)
        img_url = match.group(1)

        if img_url.startswith('http'):
            # 이미지 다운로드
            filename = download_image(img_url, output_dir)
            if filename:
                # 상대 경로로 변경
                new_tag = full_tag.replace(img_url, f'images/{filename}')
                return new_tag
        return full_tag
    
    # 이미지 태그 처리
    processed_html = re.sub(img_pattern, replace_img, html_content)
    return processed_html

def convert_markdown_to_html(md_content, output_dir):
    # 마크다운 테이블을 HTML로 변환하기 위한 확장 추가
    extensions = ['tables', 'fenced_code', 'codehilite']
    
    # 마크다운을 HTML로 변환
    html_content = markdown.markdown(md_content, extensions=extensions)
    
    # 이미지 처리
    html_content = process_images(html_content, output_dir)
    
    # 테이블 스타일 추가
    html_content = f"""
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
            body {{
                font-family: sans-serif;
                line-height: 1.6;
                padding: 16px;
            }}
            table {{
                width: 100%;
                border-collapse: collapse;
                margin: 16px 0;
                overflow-x: auto;
                display: block;
            }}
            th, td {{
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
            }}
            th {{
                background-color: #f5f5f5;
                font-weight: bold;
            }}
            tr:nth-child(even) {{
                background-color: #f9f9f9;
            }}
            tr:hover {{
                background-color: #f0f0f0;
            }}
            img {{
                max-width: 100%;
                height: auto;
            }}
        </style>
    </head>
    <body>
        {html_content}
    </body>
    </html>
    """
    return html_content

def process_directory(input_dir, output_dir):
    # 이미지 저장 디렉토리 생성
    images_dir = os.path.join(output_dir, 'images')
    os.makedirs(images_dir, exist_ok=True)
    
    # 입력 디렉토리의 모든 마크다운 파일 처리
    for root, dirs, files in os.walk(input_dir):
        # 'images' 디렉토리는 건너뛰기
        if 'images' in dirs:
            dirs.remove('images')
            
        for file in files:
            if file.endswith('.md'):
                # 입력 파일 경로
                input_path = os.path.join(root, file)
                
                # 상대 경로 계산
                rel_path = os.path.relpath(root, input_dir)
                
                # 출력 디렉토리 생성
                output_subdir = os.path.join(output_dir, rel_path)
                os.makedirs(output_subdir, exist_ok=True)
                
                # 출력 파일 경로 (.md를 .html로 변경)
                output_path = os.path.join(output_subdir, file.replace('.md', '.html'))
                
                # 마크다운 파일 읽기
                with open(input_path, 'r', encoding='utf-8') as f:
                    md_content = f.read()
                
                # HTML로 변환
                html_content = convert_markdown_to_html(md_content, images_dir)
                
                # HTML 파일 저장
                with open(output_path, 'w', encoding='utf-8') as f:
                    f.write(html_content)
                
                print(f'변환 완료: {input_path} -> {output_path}')

def main():
    # 기본 입력 디렉토리 설정
    base_dir = 'app/src/main/posts'
    
    # app/src/main/posts 디렉토리의 모든 하위 디렉토리 순회
    for category in os.listdir(base_dir):
        input_dir = os.path.join(base_dir, category)
        
        if os.path.isdir(input_dir):
            # 출력 디렉토리를 입력 디렉토리와 동일하게 설정
            output_dir = input_dir
            
            # 디렉토리 처리
            process_directory(input_dir, output_dir)

if __name__ == '__main__':
    main()