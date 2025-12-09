import { useState } from "react";

export default function Qa() {
  const [question, setQuestion] = useState("");
  const [answer, setAnswer] = useState("");

  const handleAsk = async () => {
    const res = await fetch(
      `http://localhost:8080/api/qa?question=${encodeURIComponent(question)}`
    );
    const data = await res.text();
    setAnswer(data);
  };

  return (
    <div>
      <h2 className="text-xl font-semibold mb-2">Ask a Question</h2>
      <input
        className="w-full p-2 border rounded-lg mb-3"
        type="text"
        placeholder="Ask a question..."
        value={question}
        onChange={(e) => setQuestion(e.target.value)}
      />
      <button
        onClick={handleAsk}
        className="px-4 py-2 bg-blue-500 text-white rounded-xl shadow"
      >
        Ask
      </button>
      {answer && <p className="mt-4 p-3 bg-gray-100 rounded-lg">{answer}</p>}
    </div>
  );
}
