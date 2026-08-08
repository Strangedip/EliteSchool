import { definePreset } from '@primeuix/themes';
import Aura from '@primeuix/themes/aura';

/** Cyan / teal palette — replaces Aura’s default green primary & success. */
const eliteCyan = {
  50: '#ecfeff',
  100: '#cffafe',
  200: '#a5f3fc',
  300: '#67e8f9',
  400: '#22d3ee',
  500: '#00b4d8',
  600: '#0077b6',
  700: '#0e7490',
  800: '#155e75',
  900: '#164e63',
  950: '#083344'
};

/** Soft teal for “success” (not Aura green #22c55e). */
const eliteSuccess = {
  50: '#f0fdfa',
  100: '#ccfbf1',
  200: '#99f6e4',
  300: '#5eead4',
  400: '#2dd4bf',
  500: '#0e9f6e',
  600: '#0d9488',
  700: '#0f766e',
  800: '#115e59',
  900: '#134e4a',
  950: '#042f2e'
};

/**
 * EliteSchool Aura preset: brand cyan primary, teal success, syncs with [data-theme].
 */
export const EliteAura = definePreset(Aura, {
  semantic: {
    primary: eliteCyan,
    // PrimeUIX Semantic typings omit palette extensions; runtime tokens still accept success.
    ...({ success: eliteSuccess } as object),
    colorScheme: {
      light: {
        primary: {
          color: '{primary.600}',
          contrastColor: '#ffffff',
          hoverColor: '{primary.700}',
          activeColor: '{primary.800}'
        },
        highlight: {
          background: '{primary.100}',
          focusBackground: '{primary.200}',
          color: '{primary.700}',
          focusColor: '{primary.800}'
        }
      },
      dark: {
        primary: {
          color: '{primary.400}',
          contrastColor: '{surface.900}',
          hoverColor: '{primary.300}',
          activeColor: '{primary.200}'
        },
        highlight: {
          background: 'color-mix(in srgb, {primary.400}, transparent 84%)',
          focusBackground: 'color-mix(in srgb, {primary.400}, transparent 76%)',
          color: 'rgba(255,255,255,.87)',
          focusColor: 'rgba(255,255,255,.87)'
        }
      }
    }
  }
});
