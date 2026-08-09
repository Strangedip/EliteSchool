import { Component, ChangeDetectionStrategy, Input } from '@angular/core';

@Component({
  selector: 'app-brand-mark',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    '[style.width.px]': 'size',
    '[style.height.px]': 'size'
  },
  template: `
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 64 64"
      role="img"
      [attr.aria-label]="alt || null"
      [attr.aria-hidden]="alt ? null : 'true'">
      <defs>
        <linearGradient [attr.id]="gradId" x1="8" y1="6" x2="56" y2="58" gradientUnits="userSpaceOnUse">
          <stop offset="0" stop-color="var(--brand-mark-from)"/>
          <stop offset="0.45" stop-color="var(--brand-mark-mid)"/>
          <stop offset="1" stop-color="var(--brand-mark-to)"/>
        </linearGradient>
      </defs>
      <rect width="64" height="64" rx="15" [attr.fill]="'url(#' + gradId + ')'"/>
      <rect x="14" y="42" width="10" height="11" rx="2.4" fill="var(--brand-mark-fg)" fill-opacity=".88"/>
      <rect x="27" y="33" width="10" height="20" rx="2.4" fill="var(--brand-mark-fg)"/>
      <rect x="40" y="26" width="11" height="27" rx="2.4" fill="var(--brand-mark-fg)"/>
      <path
        fill="var(--brand-mark-star)"
        transform="translate(45.5 14)"
        d="M0-5.4 1.55-1.15H6.05L2.4 1.5 3.8 5.75 0 3.15l-3.8 2.6 1.4-4.25L-6.05-1.15H-1.55Z"/>
    </svg>
  `,
  styles: [`
    :host {
      display: inline-flex;
      flex-shrink: 0;
      line-height: 0;
      border-radius: 23%;
      overflow: hidden;
    }

    svg {
      display: block;
      width: 100%;
      height: 100%;
    }
  `]
})
export class BrandMarkComponent {
  @Input() size = 40;
  @Input() alt = '';

  readonly gradId = `es-bg-${Math.random().toString(36).slice(2, 9)}`;
}
