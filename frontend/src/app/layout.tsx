import './globals.css';
import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'REST Markdown',
  description: 'Generate REST requests from plain prompts',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
