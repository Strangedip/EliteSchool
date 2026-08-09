const fs = require('fs');
const path = require('path');
const sharp = require('sharp');
const toIco = require('to-ico');

async function main() {
  const root = path.join(__dirname, '..');
  const svgPath = path.join(root, 'src/assets/images/favicon.svg');
  const markPath = path.join(root, 'src/assets/images/brand-mark.svg');
  const outDir = path.join(root, 'src/assets/images');

  const svg = fs.readFileSync(svgPath);
  const mark = fs.readFileSync(markPath);

  async function png(input, size) {
    return sharp(input).resize(size, size, { fit: 'contain', background: { r: 0, g: 0, b: 0, alpha: 0 } }).png().toBuffer();
  }

  const p16 = await png(svg, 16);
  const p32 = await png(svg, 32);
  const p48 = await png(svg, 48);
  const p180 = await png(mark, 180);
  const p192 = await png(mark, 192);
  const p512 = await png(mark, 512);

  fs.writeFileSync(path.join(outDir, 'favicon-16.png'), p16);
  fs.writeFileSync(path.join(outDir, 'favicon-32.png'), p32);
  fs.writeFileSync(path.join(outDir, 'apple-touch-icon.png'), p180);
  fs.writeFileSync(path.join(outDir, 'icon-192.png'), p192);
  fs.writeFileSync(path.join(outDir, 'icon-512.png'), p512);

  const ico = await toIco([p16, p32, p48]);
  fs.writeFileSync(path.join(root, 'src/favicon.ico'), ico);

  console.log('OK', {
    ico: fs.statSync(path.join(root, 'src/favicon.ico')).size,
    apple: fs.statSync(path.join(outDir, 'apple-touch-icon.png')).size
  });
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
