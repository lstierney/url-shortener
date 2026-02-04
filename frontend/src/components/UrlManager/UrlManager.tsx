import { useEffect, useState } from "react";
import ShortenUrl from "../ShortenUrl/ShortenUrl";
import ManageUrls, {type UrlEntry } from "../ManageUrls/ManageUrls.tsx";
import { API_BASE } from "../../../api.ts";
import Card from "../Card/Card.tsx";

const UrlManager = () => {
  const [urls, setUrls] = useState<UrlEntry[]>([]);

  const loadUrls = async () => {
    const res = await fetch(`${API_BASE}/urls`);
    const data = await res.json();
    setUrls(data);
  };

  useEffect(() => {
    loadUrls();
  }, []);

  const handleCreated = (newUrl: UrlEntry) => {
    setUrls((prev) => [...prev, newUrl]);
  };

  const handleDelete = async (alias: string) => {
    const res = await fetch(`${API_BASE}/${alias}`, { method: "DELETE" });
    if (res.status === 204) {
      setUrls((prev) => prev.filter((u) => u.alias !== alias));
    }
  };

  return (
    <>
      <Card title="Shorten URL">
        <ShortenUrl onCreated={handleCreated} />
      </Card>
      <Card title="Manage Shortened URLs">
        <ManageUrls urls={urls} onDelete={handleDelete} />
      </Card>
    </>
  );
};

export default UrlManager;
