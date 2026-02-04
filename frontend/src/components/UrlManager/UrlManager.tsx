import { useEffect, useState } from "react";
import ShortenUrl from "../ShortenUrl/ShortenUrl";
import ManageUrls from "../ManageUrls/ManageUrls";
import { listUrls, deleteUrl } from "../../api/api";
import type { UrlEntry } from "../../api/types";

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
        <ShortenUrl onCreated={handleCreated} />
        <ManageUrls urls={urls} onDelete={handleDelete} />
      </>
  );
};

export default UrlManager;
