import React, { useState, useEffect, useRef } from "react";

export default function TripPlanner() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [isTyping, setIsTyping] = useState(false); // 🔹 new state
  const chatEndRef = useRef(null);

  const sendMessage = async () => {
    if (!input.trim()) return;

    // Add user message
    setMessages((prev) => [...prev, { sender: "user", text: input }]);
    setInput("");
    setIsTyping(true); // 🔹 show typing indicator

    try {
      const res = await fetch("http://localhost:8080/api/mcp/agent/ask", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ request: input }),
      });

      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
      }

      const data = await res.json();
      console.log("Backend response:", data);

      if (data.response) {
        setMessages((prev) => [...prev, { sender: "ai", text: data.response }]);
      } else {
        setMessages((prev) => [
          ...prev,
          { sender: "ai", text: "⚠️ Unexpected response from server" },
        ]);
      }
    } catch (err) {
      setMessages((prev) => [
        ...prev,
        { sender: "ai", text: "⚠️ Error contacting backend" },
      ]);
    } finally {
      setIsTyping(false); // 🔹 hide typing indicator
    }
  };

  // 🔽 Auto scroll to bottom
  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, isTyping]);

  return (
    <div className="flex flex-col h-screen bg-gray-100">
      <h2 className="text-xl font-semibold mb-2 p-2 bg-blue-600 text-white shadow">
        Travel Agent (MCP)
      </h2>

      {/* Chat window */}
      <div className="flex-1 overflow-y-auto p-4 space-y-3 max-h-[75vh]">
        {messages.map((msg, i) => (
          <div
            key={i}
            className={`flex ${
              msg.sender === "user" ? "justify-end" : "justify-start"
            }`}
          >
            <div
              className={`p-3 rounded-2xl max-w-md shadow whitespace-pre-line ${
                msg.sender === "user"
                  ? "bg-blue-500 text-white"
                  : "bg-white text-green-700 border border-gray-200"
              }`}
            >
              {msg.text}
            </div>
          </div>
        ))}

        {/* 🔹 Typing indicator */}
{isTyping && (
  <div className="flex justify-start">
    <div className="p-3 rounded-2xl max-w-xs shadow bg-gray-200 text-gray-600 flex items-center space-x-1">
      <span className="typing-dot"></span>
      <span className="typing-dot"></span>
      <span className="typing-dot"></span>
    </div>
  </div>
)}

        <div ref={chatEndRef} />
      </div>

      {/* Input box */}
      <div className="p-3 border-t bg-white flex">
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Ask for a trip plan..."
          className="flex-1 p-2 border rounded-xl focus:outline-none"
        />
        <button
          onClick={sendMessage}
          className="ml-2 px-4 py-2 bg-blue-500 text-white rounded-xl shadow hover:bg-blue-600"
        >
          Send
        </button>
      </div>
    </div>
  );
}
