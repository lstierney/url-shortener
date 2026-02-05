import { useEffect, useState } from "react";
import ShortenUrl from "../ShortenUrl/ShortenUrl";
import ManageUrls from "../ManageUrls/ManageUrls";
import { listUrls, deleteUrl } from "../../api/api";
import type { UrlEntry } from "../../api/types";
import Card from "../Card/Card.tsx";

const UrlManager = () => {
  const [urls, setUrls] = useState<UrlEntry[]>([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    try {
      const data = await listUrls();
      setUrls(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const handleCreated = (entry: UrlEntry) => {
    setUrls((prev) => [...prev, entry]);
  };

  const handleDelete = async (alias: string) => {
    await deleteUrl(alias);
    setUrls((prev) => prev.filter((u) => u.alias !== alias));
  };

  if (loading) return <div>Loading…</div>;

  return (
      <>
        <Card title="Create a short link" subtitle="Paste a URL and optionally add a custom alias" elevated>
          <ShortenUrl onCreated={handleCreated} />
        </Card>

        <Card
            title="Your short links"
            variant="outlined"
            elevated
        >
          <ManageUrls urls={urls} onDelete={handleDelete} />
        </Card>
      </>
  );
};

export default UrlManager;
