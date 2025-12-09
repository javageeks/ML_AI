import { useState } from "react";

export default function Summarizer() {
  const [text, setText] = useState("");
  const [result, setResult] = useState("");

  const handleSummarize = async () => {
    const res = await fetch(
      `http://localhost:8080/api/summarize?text=${encodeURIComponent(text)}`
    );
    const data = await res.text();
    setResult(data);
  };

  return (
    <div>
      <h2 className="text-xl font-semibold mb-2">Summarize Text</h2>
      <textarea
        className="w-full p-2 border rounded-lg mb-3"
        rows="4"
        placeholder="Enter text to summarize..."
        value={text}
        onChange={(e) => setText(e.target.value)}
      />
      <button
        onClick={handleSummarize}
        className="px-4 py-2 bg-blue-500 text-white rounded-xl shadow"
      >
        Summarize
      </button>
      {result && <p className="mt-4 p-3 bg-gray-100 rounded-lg">{result}</p>}
    </div>
  );
}
