from pathlib import Path
from reportlab.lib import colors
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import inch
from reportlab.platypus import Image, KeepTogether, PageBreak, Paragraph, Spacer, Table, TableStyle
from reportlab.pdfbase.pdfmetrics import stringWidth
from reportlab.pdfgen import canvas

ROOT = Path(__file__).resolve().parent
OUT = ROOT / 'output' / 'pdf' / 'routeed-end-to-end-qa-report.pdf'
SCREENSHOTS = [
    Path(r'C:\Users\GEETHA~1\AppData\Local\Temp\codex-clipboard-089d2bd2-e18b-4674-b772-b4ef24f95b70.png'),
    Path(r'C:\Users\GEETHA~1\AppData\Local\Temp\codex-clipboard-83211dc9-7d70-42be-8856-4af8b31ec073.png'),
    Path(r'C:\Users\GEETHA~1\AppData\Local\Temp\codex-clipboard-8644386f-718e-488a-9f0e-58f9569cd45e.png'),
]

class ReportCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.pages = []
    def showPage(self):
        self.pages.append(dict(self.__dict__))
        self._startPage()
    def save(self):
        page_count = len(self.pages)
        for state in self.pages:
            self.__dict__.update(state)
            self.setStrokeColor(colors.HexColor('#D9E1F2'))
            self.line(40, 34, A4[0] - 40, 34)
            self.setFillColor(colors.HexColor('#667085'))
            self.setFont('Helvetica', 8)
            self.drawString(40, 20, 'RouteEd - End-to-End QA Report')
            footer = f'28 October 2026  |  Page {self._pageNumber} of {page_count}'
            self.drawRightString(A4[0] - 40, 20, footer)
            super().showPage()
        super().save()

styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name='TitleCustom', parent=styles['Title'], fontName='Helvetica-Bold', fontSize=27, leading=31, textColor=colors.HexColor('#172B4D'), spaceAfter=9))
styles.add(ParagraphStyle(name='SubTitle', parent=styles['Normal'], fontSize=11, leading=16, textColor=colors.HexColor('#475467'), spaceAfter=14))
styles.add(ParagraphStyle(name='H1Custom', parent=styles['Heading1'], fontName='Helvetica-Bold', fontSize=17, leading=22, textColor=colors.HexColor('#172B4D'), spaceBefore=5, spaceAfter=8))
styles.add(ParagraphStyle(name='H2Custom', parent=styles['Heading2'], fontName='Helvetica-Bold', fontSize=12, leading=16, textColor=colors.HexColor('#344054'), spaceBefore=7, spaceAfter=5))
styles.add(ParagraphStyle(name='BodyCustom', parent=styles['BodyText'], fontSize=9.3, leading=14, textColor=colors.HexColor('#344054'), spaceAfter=7))
styles.add(ParagraphStyle(name='Small', parent=styles['BodyText'], fontSize=8, leading=10.5, textColor=colors.HexColor('#667085')))

def p(text, style='BodyCustom'):
    return Paragraph(text, styles[style])

def table(rows, widths, header=True):
    body_style = ParagraphStyle('TableBody', parent=styles['Small'], fontSize=8.1, leading=10.2, textColor=colors.HexColor('#344054'))
    head_style = ParagraphStyle('TableHead', parent=body_style, fontName='Helvetica-Bold', textColor=colors.HexColor('#172B4D'))
    formatted = [[Paragraph(str(cell), head_style if header and row_index == 0 else body_style) for cell in row] for row_index, row in enumerate(rows)]
    t = Table(formatted, colWidths=widths, repeatRows=1 if header else 0, hAlign='LEFT')
    commands = [
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('GRID', (0,0), (-1,-1), 0.35, colors.HexColor('#D0D5DD')),
        ('LEFTPADDING', (0,0), (-1,-1), 6), ('RIGHTPADDING', (0,0), (-1,-1), 6),
        ('TOPPADDING', (0,0), (-1,-1), 5), ('BOTTOMPADDING', (0,0), (-1,-1), 5),
        ('FONTNAME', (0,0), (-1,-1), 'Helvetica'), ('FONTSIZE', (0,0), (-1,-1), 8.2),
        ('TEXTCOLOR', (0,0), (-1,-1), colors.HexColor('#344054')),
    ]
    if header:
        commands += [('BACKGROUND', (0,0), (-1,0), colors.HexColor('#EAF0FF')), ('FONTNAME', (0,0), (-1,0), 'Helvetica-Bold'), ('TEXTCOLOR', (0,0), (-1,0), colors.HexColor('#172B4D'))]
    t.setStyle(TableStyle(commands))
    return t

def screenshot(path, caption, scale=1.0):
    im = Image(str(path))
    max_w, max_h = 7.0 * inch * scale, 4.55 * inch * scale
    ratio = min(max_w / im.imageWidth, max_h / im.imageHeight)
    im.drawWidth, im.drawHeight = im.imageWidth * ratio, im.imageHeight * ratio
    return KeepTogether([im, Spacer(1, 4), p(caption, 'Small')])

story = []
story += [p('RouteEd', 'TitleCustom'), p('End-to-End QA Report', 'H1Custom'), p('Test execution: 28 October 2026 | Environment: React/Vite, Spring Boot, PostgreSQL 16, Docker, local Ollama llama3.2:3b', 'SubTitle')]
story += [p('Outcome', 'H2Custom'), p('<b>Core learner and administrator flows passed after remediation.</b> The reported assessment error was traced to a frontend timeout while the local model generated a route. The timeout was raised from 12 seconds to 90 seconds, and both starter diagnostics were expanded from two to six questions.'), Spacer(1, 4)]
story.append(table([
    ['Area', 'Result', 'Evidence'],
    ['Platform health', 'PASS', 'API health endpoint UP; PostgreSQL, Ollama, and API Docker services running.'],
    ['Authentication', 'PASS', 'New learner and administrator registrations, login token, and /auth/me verified.'],
    ['Admin builder', 'PASS', 'Created course, module, diagnostic, and question through protected APIs.'],
    ['Learner diagnostic', 'PASS', 'Six-question Java diagnostic fetched; submission returned a score and AI path.'],
    ['Progress and analytics', 'PASS', 'Module completion, 100% course progress, admin analytics, and one health score verified.'],
    ['Build checks', 'PASS WITH WARNING', 'Frontend build/lint pass; Maven build passes but has no automated tests.'],
], [1.30*inch, 1.20*inch, 4.65*inch]))
story += [Spacer(1, 13), p('Key measured result', 'H2Custom'), p('A complete custom course flow submitted in <b>5.56 seconds</b>, generated one personalized route module, recorded 100% progress after completion, and returned one analytics learner plus one health score. A Java diagnostic run measured 12.36 seconds before the timeout fix, validating the reported failure mechanism.')]

story.append(PageBreak())
story += [p('Issue evidence and remediation', 'H1Custom'), p('The following screenshots were supplied during QA. They document the pre-fix UI symptoms, not the post-fix result.'), Spacer(1, 3)]
story.append(screenshot(SCREENSHOTS[0], 'Figure 1. Assessment submission error supplied by the user. Root cause: the previous 12-second client timeout could expire while Ollama generated the personalized route.'))
story += [Spacer(1, 8), p('Remediation applied', 'H2Custom'), p('The Axios API client now waits up to 90 seconds for local AI-backed operations. Assessment errors also preserve an API-supplied message where available. This keeps the learner in the intended workflow instead of showing a false failure.')]

story.append(PageBreak())
story += [p('UI evidence and content coverage', 'H1Custom')]
story.append(screenshot(SCREENSHOTS[1], 'Figure 2. Login input contrast issue supplied by the user. The dark fields made entered values and placeholders difficult to read.'))
story += [Spacer(1, 7), p('UI remediation', 'H2Custom'), p('Input styles now define explicit slate text and placeholder colors for both light and dark themes. Java Foundations and Python Foundations each now have six diagnostic questions covering their respective skill tags.')]
story.append(screenshot(SCREENSHOTS[2], 'Figure 3. Learner workspace screenshot supplied during QA. The diagnostic call-to-action is visible, but the prior submission failure blocked route generation.', 0.52))

story.append(PageBreak())
story += [p('Open defects and recommended next actions', 'H1Custom'), p('Nine tracked findings are recorded in bugs.md. Three were fixed during this QA pass. The highest-priority open items are listed below.')]
story.append(table([
    ['ID', 'Severity', 'Finding', 'Recommendation'],
    ['BUG-004', 'Critical', 'Public registration accepts the ADMIN role.', 'Allow public learner signup only; use protected admin invite/provisioning.'],
    ['BUG-005', 'High', 'Seeded Java/Python courses do not have an assigned admin, blocking their analytics.', 'Assign a seed admin or enforce organisation-level analytics permissions.'],
    ['BUG-006', 'High', 'Maven reports no tests to run.', 'Add Spring Boot integration and security test coverage.'],
    ['BUG-007', 'Medium', 'No test SMTP service is in Docker Compose.', 'Add Mailpit and surface delivery failures to admins.'],
    ['BUG-008', 'Medium', 'Refresh-token rotation is not implemented.', 'Add rotation, revocation, and a client refresh interceptor.'],
], [0.68*inch, 0.72*inch, 2.35*inch, 3.40*inch]))
story += [Spacer(1, 13), p('QA limitations', 'H2Custom'), p('Browser UI automation was unavailable in this workstation session, so visual findings use the supplied screenshots and source/build review. API end-to-end scenarios ran against the live local Docker stack. SMTP delivery was not sent because no development mail server or test mailbox is configured.'), p('Deliverables', 'H2Custom'), p('This PDF summarizes verification and findings. The companion bugs.md file contains reproduction details, impact, status, and recommended fixes.')]

from reportlab.platypus import SimpleDocTemplate
doc = SimpleDocTemplate(str(OUT), pagesize=A4, rightMargin=40, leftMargin=40, topMargin=42, bottomMargin=48, title='RouteEd End-to-End QA Report', author='RouteEd QA')
doc.build(story, canvasmaker=ReportCanvas)
print(OUT)
