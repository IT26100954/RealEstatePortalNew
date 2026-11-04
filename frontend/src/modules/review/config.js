export default {
  "title": "Reviews & feedback",
  "singular": "review",
  "endpoint": "reviews",
  "public": true,
  "fields": [
    {
      "name": "propertyId",
      "label": "Property",
      "type": "property"
    },
    {
      "name": "rating",
      "label": "Rating (1\u20135)",
      "type": "number",
      "numeric": true,
      "min": 1,
      "maximum": 5,
      "step": 1
    },
    {
      "name": "comment",
      "label": "Comment",
      "type": "textarea"
    }
  ],
  "columns": [
    "propertyId",
    "rating",
    "comment"
  ],
  "owner": "reviewerId",
  "key": "review"
};
