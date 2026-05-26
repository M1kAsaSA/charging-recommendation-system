from pathlib import Path
from docx import Document

src = Path(r"c:/charging-recommendation-system/论文/示例 毕业设计（论文）撰写规范2025(1)(1).docx")
out = Path(r"c:/charging-recommendation-system/论文/示例_排版规则提取.txt")

if not src.exists():
    raise FileNotFoundError(src)

doc = Document(str(src))

lines = []
lines.append("# Section margins (cm)")
for i, section in enumerate(doc.sections, 1):
    lines.append(f"Section {i}:")
    lines.append(f"  top={section.top_margin.cm:.2f}")
    lines.append(f"  bottom={section.bottom_margin.cm:.2f}")
    lines.append(f"  left={section.left_margin.cm:.2f}")
    lines.append(f"  right={section.right_margin.cm:.2f}")
    lines.append(f"  header_distance={section.header_distance.cm:.2f}")
    lines.append(f"  footer_distance={section.footer_distance.cm:.2f}")

lines.append("")
lines.append("# Styles (font name/size/spacing if set)")
for style in doc.styles:
    if style.type != 1:  # paragraph styles only
        continue
    name = style.name
    font = style.font
    size = font.size.pt if font.size else None
    fname = font.name
    bold = font.bold
    italic = font.italic
    p = style.paragraph_format
    line_spacing = p.line_spacing
    space_before = p.space_before.pt if p.space_before else None
    space_after = p.space_after.pt if p.space_after else None
    lines.append(
        f"{name}: font={fname}, size={size}, bold={bold}, italic={italic}, "
        f"line_spacing={line_spacing}, before={space_before}, after={space_after}"
    )

out.write_text("\n".join(lines), encoding="utf-8")
print(f"Wrote {out}")
