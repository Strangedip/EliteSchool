export function parseLines(text: string | null | undefined): string[] {
  return (text || '')
    .split(/\r?\n/)
    .map(s => s.trim())
    .filter(Boolean);
}
