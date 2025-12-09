import { useState } from "react";

export default function ToolSearch() {
  const [query, setQuery] = useState("");
  const [result, setResult] = useState(null);

  const handleSearch = async () => {
    try {
      const res = await fetch("http://localhost:8181/api/mcp/search", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ question: query }),
      });
      const data = await res.json();
      setResult(data);
    } catch (err) {
      setResult({ source: "Error", answer: err.message });
    }
  };

  return (
    <div>
      <h2 className="text-xl font-semibold mb-2">Tool Search</h2>
      <input
        className="w-full p-2 border rounded-lg mb-3"
        type="text"
        placeholder="Enter search query..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
      />
      <button
        onClick={handleSearch}
        className="px-4 py-2 bg-blue-500 text-white rounded-xl shadow"
      >
        Search
      </button>

      {result && (
        <div className="mt-4 p-4 bg-gray-100 rounded-lg">
          <p className="text-sm text-gray-600">Source: {result.source}</p>
          <p className="mt-2 font-medium whitespace-pre-line">
            {result.answer}
          </p>
        </div>
      )}
    </div>
  );
}
