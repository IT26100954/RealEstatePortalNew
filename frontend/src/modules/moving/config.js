export default {
  "title": "Moving services",
  "singular": "moving company",
  "endpoint": "moving-companies",
  "public": true,
  "fields": [
    {
      "name": "companyName",
      "label": "Company name",
      "type": "text",
      "max": 150
    },
    {
      "name": "email",
      "label": "Email",
      "type": "email",
      "max": 254
    },
    {
      "name": "phone",
      "label": "Phone",
      "type": "tel",
      "max": 20
    },
    {
      "name": "serviceAreas",
      "label": "Service areas",
      "type": "text",
      "max": 500
    },
    {
      "name": "description",
      "label": "Description",
      "type": "textarea"
    },
    {
      "name": "availability",
      "label": "Availability",
      "options": [
        "AVAILABLE",
        "UNAVAILABLE"
      ]
    }
  ],
  "columns": [
    "companyName",
    "serviceAreas",
    "phone",
    "availability"
  ],
  "key": "moving"
};
