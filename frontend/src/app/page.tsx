'use client';

import { useState } from 'react';

export default function Home() {
  const [prompt, setPrompt] = useState('');
  const [markdown, setMarkdown] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    const res = await fetch('http://localhost:8080/api/markdown', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ prompt }),
    });
    const data = await res.json();
    setMarkdown(data.markdown);
    setLoading(false);
  }

  return (
    <main className="min-h-screen bg-gradient-to-br from-slate-950 via-slate-900 to-slate-800 p-6">
      <div className="mx-auto flex max-w-5xl flex-col gap-6">
        <header className="rounded-2xl border border-slate-800 bg-slate-900/70 p-8 shadow-2xl">
          <p className="text-sm uppercase tracking-[0.3em] text-cyan-400">REST Markdown</p>
          <h1 className="mt-3 text-4xl font-semibold">Turn prompts into REST request markdown</h1>
          <p className="mt-3 max-w-2xl text-slate-300">
            This starter app combines a Spring Boot API with a polished Next.js + Tailwind interface.
          </p>
        </header>

        <section className="grid gap-6 lg:grid-cols-[1fr_1.2fr]">
          <form onSubmit={handleSubmit} className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6 shadow-xl">
            <label className="text-sm font-medium text-slate-300">Describe the endpoint you want</label>
            <textarea
              className="mt-3 min-h-40 w-full rounded-xl border border-slate-700 bg-slate-950 p-3 text-slate-100 outline-none ring-0"
              placeholder="Create a GET endpoint for fetching users"
              value={prompt}
              onChange={(e) => setPrompt(e.target.value)}
            />
            <button
              type="submit"
              className="mt-4 rounded-xl bg-cyan-500 px-4 py-2 font-semibold text-slate-950 transition hover:bg-cyan-400"
            >
              {loading ? 'Generating...' : 'Generate'}
            </button>
          </form>

          <div className="rounded-2xl border border-slate-800 bg-slate-900/70 p-6 shadow-xl">
            <h2 className="text-lg font-semibold">Generated markdown</h2>
            <pre className="mt-4 whitespace-pre-wrap break-words rounded-xl bg-slate-950 p-4 text-sm text-slate-300">
              {markdown || 'Your generated markdown will appear here.'}
            </pre>
          </div>
        </section>
      </div>
    </main>
  );
}
