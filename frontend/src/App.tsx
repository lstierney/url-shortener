import { useState } from "react";

const App = () => {
    const [url, setUrl] = useState("");
    const [shortUrl, setShortUrl] = useState("");

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        // TODO call backend here
        console.log("URL to shorten:", url);

        // Temporary placeholder until backend is connected
        setShortUrl("http://localhost:8080/short/abc123");
    };

    return (
        <div style={{ padding: "2rem", fontFamily: "sans-serif", maxWidth: 600 }}>
            <h1>URL Shortener</h1>

            <form onSubmit={handleSubmit} style={{ marginTop: "1rem" }}>
                <input
                    type="text"
                    placeholder="Enter a long URL..."
                    value={url}
                    onChange={(e) => setUrl(e.target.value)}
                    style={{
                        width: "100%",
                        padding: "0.5rem",
                        fontSize: "1rem",
                        marginBottom: "1rem",
                    }}
                />

                <button
                    type="submit"
                    style={{
                        padding: "0.5rem 1rem",
                        fontSize: "1rem",
                        cursor: "pointer",
                    }}
                >
                    Shorten URL
                </button>
            </form>

            {shortUrl && (
                <div style={{ marginTop: "1.5rem" }}>
                    <strong>Shortened URL:</strong>
                    <div>
                        <a href={shortUrl} target="_blank" rel="noopener noreferrer">
                            {shortUrl}
                        </a>
                    </div>
                </div>
            )}
        </div>
    );
};

export default App;
