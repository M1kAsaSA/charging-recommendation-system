from pathlib import Path
from collections import Counter
from docx import Document

src = Path(r"c:/charging-recommendation-system/论文/示例 毕业设计（论文）撰写规范2025(1)(1).docx")

if not src.exists():
    raise FileNotFoundError(src)

doc = Document(str(src))

line_spacing = Counter()
space_before = Counter()
space_after = Counter()
style_counter = Counter()

for p in doc.paragraphs:
    style_counter[p.style.name] += 1
    fmt = p.paragraph_format
    line_spacing[fmt.line_spacing] += 1
    space_before[fmt.space_before.pt if fmt.space_before else None] += 1
    space_after[fmt.space_after.pt if fmt.space_after else None] += 1

print("Top styles:")
for name, cnt in style_counter.most_common(10):
    print(name, cnt)

print("Line spacing counts:")
for k, v in line_spacing.most_common(10):
    print(k, v)

print("Space before counts:")
for k, v in space_before.most_common(10):
    print(k, v)

print("Space after counts:")
for k, v in space_after.most_common(10):
    print(k, v)
