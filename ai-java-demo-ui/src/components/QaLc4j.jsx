import { useState } from "react";

export default function QaLc4j() {
  const [question, setQuestion] = useState("");
  const [category, setCategory] = useState("All");
  const [answer, setAnswer] = useState("");
  const [loading, setLoading] = useState(false);

  const handleAsk = async () => {
    if (!question.trim()) return;

    setLoading(true);
    setAnswer("");

    try {
      const url =
        category === "All"
          ? "/api/qa-lc4j"
          : `/api/qa-lc4j?category=${encodeURIComponent(category)}`;

      const response = await fetch(url, {
        method: "POST",
        headers: { "Content-Type": "text/plain" },
        body: question,
      });

      if (!response.ok) throw new Error("Request failed");
      const data = await response.text();
      setAnswer(data);
    } catch (err) {
      console.error(err);
      setAnswer("❌ Error calling /qa-lc4j API");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2 className="text-xl font-semibold mb-4">Q&A (LangChain4j + Category Filter)</h2>

      <textarea
        className="w-full p-3 border rounded-xl mb-4"
        rows="3"
        placeholder="Ask a question..."
        value={question}
        onChange={(e) => setQuestion(e.target.value)}
      />

      <div className="flex space-x-4 mb-4">
        <select
          className="p-2 border rounded-xl"
          value={category}
          onChange={(e) => setCategory(e.target.value)}
        >
          <option value="All">All</option>
          <option value="HR">HR</option>
          <option value="General">General</option>
        </select>

        <button
          onClick={handleAsk}
          disabled={loading}
          className="px-4 py-2 bg-blue-500 text-white rounded-xl shadow disabled:opacity-50"
        >
          {loading ? "Asking..." : "Ask"}
        </button>
      </div>

      {answer && (
        <div className="p-4 bg-gray-50 border rounded-xl whitespace-pre-line">
          {answer}
        </div>
      )}
    </div>
  );
}
