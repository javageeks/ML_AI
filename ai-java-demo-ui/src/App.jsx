import { useState } from "react";
import Summarizer from "./components/Summarizer";
import Qa from "./components/Qa";
import QaLc4j from "./components/QaLc4j";
import ToolSearch from "./components/ToolSearch";
import AgentSearch from "./components/AgentSearch";


export default function App() {
  const [tab, setTab] = useState("summarize");

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <h1 className="text-3xl font-bold text-center mb-6">AI in Java Demo</h1>

      <div className="flex justify-center space-x-4 mb-6">
        <button
          onClick={() => setTab("summarize")}
          className={`px-4 py-2 rounded-xl shadow ${
            tab === "summarize" ? "bg-blue-500 text-white" : "bg-white"
          }`}
        >
          Summarizer
        </button>
        <button
          onClick={() => setTab("qa")}
          className={`px-4 py-2 rounded-xl shadow ${
            tab === "qa" ? "bg-blue-500 text-white" : "bg-white"
          }`}
        >
          Q&A
        </button>
        <button
          onClick={() => setTab("qaLangChain4j")}
          className={`px-4 py-2 rounded-xl shadow ${
            tab === "tool" ? "bg-blue-500 text-white" : "bg-white"
          }`}
        >
                    Q&A (LangChain4j)
        </button>
        <button
          onClick={() => setTab("mcptool")}
          className={`px-4 py-2 rounded-xl shadow ${
            tab === "tool" ? "bg-blue-500 text-white" : "bg-white"
          }`}
        >
          Tool Search (LLM Tool Routing)
        </button>
          <button
    onClick={() => setTab("agent")}
    className={`px-4 py-2 rounded-xl shadow ${
      tab === "agent" ? "bg-green-500 text-white" : "bg-white"
    }`}
  >
    Travel Agent(MCP)
  </button>
      </div>

      <div className="max-w-2xl mx-auto bg-white p-6 rounded-2xl shadow-lg">
        {tab === "summarize" && <Summarizer />}
        {tab === "qa" && <Qa />}
        {tab === "qaLangChain4j" && <QaLc4j />}
        {tab === "mcptool" && <ToolSearch />}
        {tab === "agent" && <AgentSearch />}
      </div>
    </div>
  );
}
