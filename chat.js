export default async function handler(req, res) {
  try {
    if (req.method !== 'POST') return res.status(405).json({ error: 'Method not allowed' });
    const { message, history } = req.body || {};
    if (!message) return res.status(400).json({ error: 'No message provided' });

    const OPENAI_KEY = process.env.OPENAI_API_KEY;
    if (!OPENAI_KEY) return res.status(500).json({ error: 'OpenAI API key not configured' });

    const payload = {
      model: 'gpt-4o-mini',
      messages: [
        { role: 'system', content: 'You are Doclix assistant. Help users create legal and official documents. Keep responses concise and practical.' },
        ...(history && Array.isArray(history) ? history.slice(-6).map(h => ({ role: h.role, content: h.content })) : [{ role: 'user', content: message }])
      ],
      max_tokens: 800
    };

    const r = await fetch('https://api.openai.com/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${OPENAI_KEY}`
      },
      body: JSON.stringify(payload)
    });

    if (!r.ok) {
      const t = await r.text();
      console.error('OpenAI error', t);
      return res.status(502).json({ error: 'OpenAI returned an error' });
    }

    const data = await r.json();
    const reply = data.choices?.[0]?.message?.content ?? data.choices?.[0]?.text ?? '(no reply)';
    return res.status(200).json({ reply });
  } catch (err) {
    console.error(err);
    return res.status(500).json({ error: 'Server error' });
  }
}
